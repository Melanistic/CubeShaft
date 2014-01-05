/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft;

import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.util.glu.GLU.*;

import java.awt.Canvas;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Random;

import javax.swing.JOptionPane;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Cursor;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

import com.melanistics.PluginManager;
import com.melanistics.TickHandler;
import com.naronco.cubeshaft.gui.Menu;
import com.naronco.cubeshaft.gui.PausedGameMenu;
import com.naronco.cubeshaft.gui.StartMenu;
import com.naronco.cubeshaft.gui.TextRenderer;
import com.naronco.cubeshaft.level.Chunk;
import com.naronco.cubeshaft.level.Cloud;
import com.naronco.cubeshaft.level.Level;
import com.naronco.cubeshaft.level.LevelIO;
import com.naronco.cubeshaft.level.LevelRenderer;
import com.naronco.cubeshaft.level.generator.LevelGenerator;
import com.naronco.cubeshaft.level.tile.Tile;
import com.naronco.cubeshaft.particle.Particle;
import com.naronco.cubeshaft.particle.ParticleEngine;
import com.naronco.cubeshaft.phys.AABB;
import com.naronco.cubeshaft.player.Player;
import com.naronco.cubeshaft.render.FrustumCuller;
import com.naronco.cubeshaft.render.Renderer;
import com.naronco.cubeshaft.render.Tesselator;
import com.naronco.cubeshaft.render.TextureLoader;
import com.naronco.cubeshaft.render.TileRenderer;

public class Cubeshaft {
	/**
	 * this class renders the Game
	 * 
	 * @author Jujuedv
	 * 
	 */
	private class Rendering implements Runnable {
		@Override
		public void run() {
			long lastFrameCountTime = System.currentTimeMillis();
			long lastTime = System.nanoTime();
			int frames = 0;
			try {
				while (running) {
					if (canvas == null && Display.isCloseRequested()) {
						running = false;
					}

					long now = System.nanoTime();
					long passedTime = now - lastTime;
					lastTime = now;
					if (passedTime < 0)
						passedTime = 0;
					if (passedTime > 1000000000)
						passedTime = 1000000000;
					if (menu != null) {
						menu.input();
						if (menu != null)
							menu.tick();
					}

					checkGLError("Pre render");
					if (Keyboard.isKeyDown(Keyboard.KEY_F12))
						try {
							Thread.sleep(30000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					render(passedTime * TICKS_PER_SECOND / 1000000000 % 1);
					checkGLError("Post render");

					frames++;
					while (System.currentTimeMillis() >= lastFrameCountTime + 1000) {
						debugInfo = frames + " fps, " + Chunk.chunkUpdates
								+ " chunk updates";
						lastFrameCountTime += 1000;
						frames = 0;
						Chunk.chunkUpdates = 0;
					}

					if (FRAMES_PER_SECOND != -1) {
						try {
							long neededTime = System.nanoTime() - lastTime;
							long wait = (long) Math
									.round(1000f / FRAMES_PER_SECOND
											- neededTime / 1000000f);
							Thread.sleep(wait > 0 ? wait : 0);
						} catch (InterruptedException e) {
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				running = false;
			}
		}

		public void start() {
			run();
		}
	}

	private class Ticking implements Runnable {
		private Thread thread;

		@Override
		public void run() {
			long lastTime = System.nanoTime();
			long passedTime = 0;
			try {
				while (running) {
					if (canvas == null && Display.isCloseRequested()) {
						running = false;
					}

					long now = System.nanoTime();
					passedTime = now - lastTime;
					lastTime = now;
					if (passedTime < 0)
						passedTime = 0;
					if (passedTime > 1000000000)
						passedTime = 1000000000;
					if (level != null) {
						time++;
						level.tick();
						synchronized (particleEngine.particles) {
							for (int i = 0; i < particleEngine.particles.size(); i++) {
								Particle p = particleEngine.particles.get(i);
								p.tick();
								if (p.removed)
									particleEngine.particles.remove(i--);
							}
						}

						synchronized (level.entities) {
							for (int i = 0; i < level.entities.size(); i++) {
								level.entities.get(i).tick();
								if (level.entities.get(i).y < -50)
									level.entities.get(i).removed = true;
								if (level.entities.get(i).removed)
									level.entities.remove(i--);
							}
						}

						synchronized (level.tiles) {
							for (int z = 0; z <= level.depth; z++) {
								for (int y = 0; y <= level.height; y++) {
									for (int x = 0; x <= level.width; x++) {
										if (level.getTile(x, y, z) == Tile.water.id) {
											Tile.water.tick(level, x, y, z,
													new Random());
										}
									}
								}
							}
						}

						zLight = (float) (Math.cos(level.time / 3600f * 2f
								* Math.PI) * 146.0f + level.depth / 2);
						yLight = (float) (Math.sin(level.time / 3600f * 2f
								* Math.PI) * 146.0f + level.height * 0.6f);
						yLightRot = (float) ((Math.atan2(zLight - level.depth
								/ 2, yLight - level.height / 2) + Math.PI * 0.5f)
								/ Math.PI * 180.0f);
						xLightRot = 180;

						zSunPos = (float) (Math.cos(Math.min(
								1.5 * Math.PI,
								Math.max(-1.5 * Math.PI, (level.time / 3600f
										* 2f * Math.PI - 0.5 * Math.PI)
										* 1.3 + 0.5 * Math.PI))) * 146.0f + level.depth / 2);
						ySunPos = (float) (Math.sin(Math.min(
								1.5 * Math.PI,
								Math.max(-1.5 * Math.PI, (level.time / 3600f
										* 2f * Math.PI - 0.5 * Math.PI)
										* 1.3 + 0.5 * Math.PI))) * 146.0f + level.height * 0.6f);
						/*
						 * try { long neededTime = System.nanoTime() - lastTime;
						 * long wait = (long) Math.round(1000f /
						 * TICKS_PER_SECOND - neededTime / 1000000f);
						 * Thread.sleep(wait > 0 ? wait : 0); } catch
						 * (InterruptedException e) { }
						 */
					}
					Display.sync(TICKS_PER_SECOND);
					ticker.Tick();
				}
			} catch (Exception e) {
				e.printStackTrace();
				running = false;
			}

		}

		public void start() {
			thread = new Thread(this);
			thread.start();
		}

		public void join() {
			while (true)
				try {
					thread.join();
					return;
				} catch (InterruptedException e) {
				}
		}
	}

	private class PlayerTicking implements Runnable {
		private Thread thread;

		@Override
		public void run() {
			long lastTime = System.nanoTime();
			long passedTime = 0;
			try {
				while (running) {
					if (canvas == null && Display.isCloseRequested()) {
						running = false;
					}
					if (player == null)
						continue;
					long now = System.nanoTime();
					passedTime = now - lastTime;
					lastTime = now;
					if (passedTime < 0)
						passedTime = 0;
					if (passedTime > 1000000000)
						passedTime = 1000000000;

					if (menu == null) {
						while (Mouse.next()) {
							if (!inGame && Mouse.getEventButtonState())
								setInGame();
							else if (Mouse.getEventButton() == 0
									&& Mouse.getEventButtonState()) {
								synchronized (hitResultSynchronizer) {
									if (hitResult != null) {
										if (hitResult.entity == null) {
											int tileId = level.getTile(
													hitResult.x, hitResult.y,
													hitResult.z);
											if (tileId > 0) {
												level.setTile(hitResult.x,
														hitResult.y,
														hitResult.z, 0);
												Tile tile = Tile.tiles[tileId];
												tile.destroy(level,
														hitResult.x,
														hitResult.y,
														hitResult.z,
														particleEngine);
											}
										} else {
											hitResult.entity.hit(player, 1);
										}
									}

								}
							}
							if (Mouse.getEventButton() == 1
									&& Mouse.getEventButtonState()) {
								synchronized (hitResultSynchronizer) {
									if (hitResult != null
											&& hitResult.entity == null) {
										int x = hitResult.x;
										int y = hitResult.y;
										int z = hitResult.z;
										if (hitResult.side == 0)
											y--;
										if (hitResult.side == 1)
											y++;
										if (hitResult.side == 2)
											z--;
										if (hitResult.side == 3)
											z++;
										if (hitResult.side == 4)
											x--;
										if (hitResult.side == 5)
											x++;
										AABB bb = Tile.tiles[selectedTile]
												.getAABB(x, y, z);
										if (bb == null
												|| (bb != null && !player.aabb
														.intersects(bb)))
											level.setTile(x, y, z, selectedTile);
									}
								}
							}
						}
						while (Keyboard.next()) {
							int index = -1;
							boolean keyState = Keyboard.getEventKeyState();
							int key = Keyboard.getEventKey();
							if (key == Keyboard.KEY_W || key == Keyboard.KEY_UP)
								index = 0;
							if (key == Keyboard.KEY_S
									|| key == Keyboard.KEY_DOWN)
								index = 1;
							if (key == Keyboard.KEY_A
									|| key == Keyboard.KEY_LEFT)
								index = 2;
							if (key == Keyboard.KEY_D
									|| key == Keyboard.KEY_RIGHT)
								index = 3;
							if (key == Keyboard.KEY_SPACE)
								index = 4;
							if (index >= 0)
								player.keys[index] = keyState;
							if (Keyboard.getEventKeyState()) {
								if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE)
									pauseGame();
								if (Keyboard.getEventKey() == Keyboard.KEY_F4)
									levelManager.save("save_test_world", level,
											player);
								if (Keyboard.getEventKey() == Keyboard.KEY_F5)
									levelManager.load("save_test_world", level,
											player);
								if (Keyboard.getEventKey() == Keyboard.KEY_R)
									player.resetPos();
								if (Keyboard.getEventKey() == Keyboard.KEY_Y)
									mouseDir = -mouseDir;
								if (Keyboard.getEventKey() == Keyboard.KEY_F)
									levelRenderer.viewDistance = (levelRenderer.viewDistance + 1) % 4;
								if (Keyboard.getEventKey() == Keyboard.KEY_Q)
								{
									if(player.inventory.items.size() > 0) player.inventory.items.remove(player.inventory.selectedSlot);
								}

							}
							ticker.Input(key, Keyboard.getEventCharacter(),
									keyState);
						}
						player.inventory.selectedSlot -= Mouse.getDWheel() / 120;
						if (player.inventory.selectedSlot < 0)
							player.inventory.selectedSlot = player.inventory
									.getMaxSpeedSlots() - 1;
						if (player.inventory.selectedSlot > player.inventory
								.getMaxSpeedSlots() - 1)
							player.inventory.selectedSlot = 0;
						if(player.inventory.getSelectedTile() != null)
						{
							selectedTile = player.inventory.getSelectedTile().id;
							renderer.heldTile.tile = Tile.tiles[selectedTile];
						}
					}

					player.tick();

					int dwidth = Display.getWidth();
					int dheight = Display.getHeight();
					if (dwidth != width || dheight != height) {
						width = dwidth;
						height = dheight;
					}

					try {
						long neededTime = System.nanoTime() - lastTime;
						long wait = (long) Math.round(1000f / TICKS_PER_SECOND
								- neededTime / 1000000f);
						Thread.sleep(wait > 0 ? wait : 0);
					} catch (InterruptedException e) {
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				running = false;
			}
		}

		public void start() {
			thread = new Thread(this);
			thread.start();
		}

		public void join() {
			while (true)
				try {
					thread.join();
					return;
				} catch (InterruptedException e) {
				}
		}
	}

	public static Cubeshaft game;
	public static TickHandler ticker = new TickHandler();
	private static final int TICKS_PER_SECOND = 60;
	/**
	 * tells the game how many frames it should try to render per second<br/>
	 * -1 means no limit
	 */
	private static final int FRAMES_PER_SECOND = 100;

	private static final int SHADOW_MAP_SIZE = 2048;
	private float xLight, yLight, zLight;
	private float xSunPos, ySunPos, zSunPos;
	private float xLightRot, yLightRot;
	private FloatBuffer lightProjMatrix = BufferUtils.createFloatBuffer(16);
	private FloatBuffer lightViewMatrix = BufferUtils.createFloatBuffer(16);
	private FloatBuffer inverseCameraMatrix = BufferUtils.createFloatBuffer(16);
	private int depthTexture, framebuffer;

	private Canvas canvas;
	private ParticleEngine particleEngine;
	public LevelRenderer levelRenderer;
	private Cursor inGameCursor;
	public TextRenderer guiText;
	private String debugInfo = "";
	private String progressTitle = "";
	private String progressText = "";
	private boolean loading = false;
	private Renderer renderer;
	private Shader lightShader, basicColorShader;
	private Cloud clouds;

	private boolean fullscreen = false;
	public int width;
	public int height;
	public int time;
	public Level level;
	public Player player;
	public LevelIO levelManager;
	private int selectedTile = 1;
	public boolean grabMouse = false;
	private volatile boolean running = false;
	private int mouseDir = 1;
	private Menu menu = null;
	private LevelGenerator generator = new LevelGenerator(this);
	private boolean inGame = false;
	private HitResult hitResult;
	public static final String version = "v[id]([sha1])";

	private Rendering rendering = new Rendering();
	private Ticking ticking = new Ticking();
	private PlayerTicking playerTicking = new PlayerTicking();

	private Object hitResultSynchronizer = new Object();

	public Cubeshaft(Canvas canvas, int width, int height, boolean fullscreen) {
		this.canvas = canvas;
		this.width = width;
		this.height = height;
		this.fullscreen = fullscreen;
	}

	public void setMenu(Menu menu) {
		if (this.menu != null) {
			this.menu.close();
		}
		this.menu = menu;
		if (this.menu != null) {
			this.menu.init(this, width, height);
		}
	}

	private static void checkGLError(String where) {
		int glError = glGetError();
		if (glError != 0) {
			String glErrorString = gluErrorString(glError);
			System.out.println("########## GL ERROR ##########");
			System.out.println("@ " + where);
			System.out.println(glError + ": " + glErrorString);
			System.exit(0);
		}
	}

	public void stop() {
		Mouse.destroy();
		Keyboard.destroy();
		Display.destroy();
	}

	public boolean startup() {
		try {
			if (canvas != null) {
				Display.setParent(canvas);
			} else if (fullscreen) {
				Display.setFullscreen(true);
				width = Display.getDisplayMode().getWidth();
				height = Display.getDisplayMode().getHeight();
			} else {
				Display.setDisplayMode(new DisplayMode(width, height));
				Display.setResizable(true);
			}
			Display.setTitle("Cubeshaft " + version);
			System.out.println("1");
			try {
				Display.create();
			} catch (LWJGLException e) {
				e.printStackTrace();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				Display.create();
				System.out.println("Test");
			}
			System.out.println("2");
			Keyboard.create();
			Mouse.create();
			System.out.println("3");
			glShadeModel(GL_SMOOTH);
			glClearColor(0.5f, 0.8f, 1.0f, 0);

			guiText = new TextRenderer("/default.png");
			startUpWorld();
			regenLevel();

			pauseGame();
			// setMenu(new StartMenu());
			System.out.println("4");
			checkGLError("Post startup");
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.toString(),
					"Failed to start Cubeshaft", 0);
			return false;
		}

		return true;
	}

	public void startUpWorld() {
		IntBuffer cursorIntBuffer = (IntBuffer) BufferUtils
				.createIntBuffer(256).clear().limit(256);

		level = new Level();
		lightShader = new Shader("light");
		basicColorShader = new Shader("basiccolor");
		player = new Player(level);
		levelRenderer = new LevelRenderer(level);
		particleEngine = new ParticleEngine();
		levelManager = new LevelIO(this);
		renderer = new Renderer(this);
		clouds = new Cloud();
		inGame = true;
		TileRenderer.init(this);

		xLight = level.width / 2;
		yLight = 90;
		zLight = -30;
		yLightRot = 45;
		xLightRot = 180;

		xSunPos = level.width / 2;

		depthTexture = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, depthTexture);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE,
				GL_COMPARE_R_TO_TEXTURE);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LEQUAL);
		glTexParameteri(GL_TEXTURE_2D, GL_DEPTH_TEXTURE_MODE, GL_INTENSITY);

		glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, SHADOW_MAP_SIZE,
				SHADOW_MAP_SIZE, 0, GL_DEPTH_COMPONENT, GL_UNSIGNED_BYTE,
				(ByteBuffer) null);
		glBindTexture(GL_TEXTURE_2D, 0);

		framebuffer = glGenFramebuffers();
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebuffer);

		glDrawBuffer(GL_NONE);

		glFramebufferTexture2D(GL_DRAW_FRAMEBUFFER, GL_DEPTH_ATTACHMENT,
				GL_TEXTURE_2D, depthTexture, 0);
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glViewport(0, 0, width, height);
		if (grabMouse) {
			try {
				inGameCursor = new Cursor(16, 16, 0, 0, 1, cursorIntBuffer,
						null);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setInGame() {
		if (this.inGame)
			return;
		this.inGame = true;
		if (grabMouse)
			try {
				Mouse.setNativeCursor(inGameCursor);
				Mouse.setCursorPosition(this.width / 2, this.height / 2);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		else
			Mouse.setGrabbed(true);
		setMenu(null);
	}

	private void pauseGame() {
		if (!this.inGame)
			return;
		for (int i = 0; i < 10; i++)
			player.keys[i] = false;
		this.inGame = false;
		if (grabMouse)
			try {
				Mouse.setNativeCursor(null);
			} catch (LWJGLException e) {
				e.printStackTrace();
			}
		else
			Mouse.setGrabbed(false);
		setMenu(new PausedGameMenu());
	}

	private void render(float delta) {
		if (!Display.isActive())
			pauseGame();
		if (loading)
			return;
		if (level != null) {
			if (inGame) {
				float dx = 0.0f;
				float dy = 0.0f;
				dx = Mouse.getDX();
				dy = Mouse.getDY();
				if (grabMouse) {
					Display.processMessages();
					Mouse.poll();
					dx = Mouse.getX() - width / 2;
					dy = Mouse.getY() - height / 2;
					Mouse.setCursorPosition(width / 2, height / 2);
				}
				player.xRot += (float) (dx * 0.15);
				player.yRot -= (float) (dy * 0.15 * mouseDir);
				if (player.yRot < -90.0f)
					player.yRot = -90.0f;
				if (player.yRot > 90.0f)
					player.yRot = 90.0f;
			}

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glClearColor((level.skyColor >> 16 & 0xff) / 255.0f,
					(level.skyColor >> 8 & 0xff) / 255.0f,
					(level.skyColor & 0xff) / 255.0f, 0);

			pick(delta);

			glEnable(GL_DEPTH_TEST);
			glDepthFunc(GL_LEQUAL);
			glShadeModel(GL_SMOOTH);

			glEnable(GL_BLEND);
			glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			glEnable(GL_ALPHA_TEST);
			glAlphaFunc(GL_GREATER, 0.0f);

			renderShadowMap(delta);

			glViewport(0, 0, width, height);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glMatrixMode(GL_PROJECTION);
			glLoadIdentity();
			gluPerspective(70.0f, width / (float) height, 0.05f, 1024.0f);
			glMatrixMode(GL_MODELVIEW);

			glLoadIdentity();
			glRotatef(player.yRot, 1.0f, 0.0f, 0.0f);
			glRotatef(player.xRot, 0.0f, 1.0f, 0.0f);
			float x = player.xo + (player.x - player.xo) * delta;
			float y = player.yo + (player.y - player.yo) * delta;
			float z = player.zo + (player.z - player.zo) * delta;
			glTranslatef(-x, -y, -z);
			/*
			 * glRotatef(yLightRot, 1.0f, 0.0f, 0.0f); glRotatef(xLightRot,
			 * 0.0f, 1.0f, 0.0f); glTranslatef(-xLight, -yLight, -zLight);
			 */

			updateChunks();

			FloatBuffer cameraMatrix = BufferUtils.createFloatBuffer(16);
			glGetFloat(GL_MODELVIEW_MATRIX, cameraMatrix);
			inverse(inverseCameraMatrix, cameraMatrix, delta);
			inverseCameraMatrix.flip();

			lightShader.enable();
			lightShader.bind("comp", 0);
			lightShader.bind("shadowMap", 7);
			lightShader.bind("sunAngle", (yLightRot + 450f) % 360 - 90f);

			glClientActiveTexture(GL_TEXTURE0);
			glActiveTexture(GL_TEXTURE0);
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D,
					TextureLoader.load("/terrain.png", GL_NEAREST));
			glClientActiveTexture(GL_TEXTURE7);
			glActiveTexture(GL_TEXTURE7);
			glEnable(GL_TEXTURE_2D);
			glBindTexture(GL_TEXTURE_2D, depthTexture);

			FloatBuffer biasMatrix = BufferUtils.createFloatBuffer(16);
			biasMatrix.put(0.5f).put(0.0f).put(0.0f).put(0.0f);
			biasMatrix.put(0.0f).put(0.5f).put(0.0f).put(0.0f);
			biasMatrix.put(0.0f).put(0.0f).put(0.5f).put(0.0f);
			biasMatrix.put(0.5f).put(0.5f).put(0.5f).put(1.0f);
			biasMatrix.flip();

			glMatrixMode(GL_TEXTURE);
			glLoadIdentity();

			glLoadMatrix(biasMatrix);
			glMultMatrix(lightProjMatrix);
			glMultMatrix(lightViewMatrix);
			glMultMatrix(inverseCameraMatrix);

			glMatrixMode(GL_MODELVIEW);

			glEnable(GL_CULL_FACE);
			glCullFace(GL_BACK);

			FloatBuffer pos = BufferUtils.createFloatBuffer(4);
			pos.put(xLight).put(yLight).put(zLight).put(1);
			pos.flip();
			glLight(GL_LIGHT0, GL_POSITION, pos);

			glClientActiveTexture(GL_TEXTURE0);
			glActiveTexture(GL_TEXTURE0);
			renderScene(delta);
			lightShader.disable();

			glBindTexture(GL_TEXTURE_2D, 0);
			glDisable(GL_TEXTURE_2D);
			glDisable(GL_CULL_FACE);

			glClientActiveTexture(GL_TEXTURE0);
			glActiveTexture(GL_TEXTURE0);

			synchronized (hitResultSynchronizer) {
				if (hitResult != null) {
					glDepthMask(false);
					LevelRenderer.renderPickBox(hitResult);
					glDepthMask(true);
				}
			}
			Tesselator t = Tesselator.instance;

			renderer.enableGuiMode();
			checkGLError("GUI: Init");

			glColor4f(1, 1, 1, 1);
			glBindTexture(GL_TEXTURE_2D,
					TextureLoader.load("/terrain.png", GL_NEAREST));
			glEnable(GL_TEXTURE_2D);

			float size = 64.0f;
			for (int i = 0; i < player.inventory.getMaxSpeedSlots(); i++) {
				glPushMatrix();
				glTranslatef((width - player.inventory.getMaxSpeedSlots()
						* size)
						/ 2.0f + i * size + size * 0.5f, height - size * 0.5f,
						-50.0f);
				glScalef(32.0f, 32.0f, 32.0f);
				if (i == player.inventory.selectedSlot) {
					glScalef(1.5f, 1.5f, 1.5f);
				}
				glRotatef(-30.0f, 1.0f, 0.0f, 0.0f);
				glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
				glTranslatef(-1.5f, 0.5f, 0.5f);
				glScalef(-1.0f, -1.0f, -1.0f);
				t.begin();
				player.inventory.items.get(i).renderGui(0, -2, 0, 0);
				t.end();
				glPopMatrix();
			}

			checkGLError("GUI: Draw selected");

			String version = "Cubeshaft " + Cubeshaft.version;
			guiText.drawString(version,
					width - TextRenderer.getTextLength(version) - 2, 2,
					0xffffff);
			guiText.drawString(debugInfo, 2, 2, 0xffffff);
			checkGLError("GUI: Draw text");
		}
		Tesselator t = Tesselator.instance;
		float xm = width / 2;
		float ym = height / 2;
		int r = 7;
		int h = 2;
		glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		t.begin();
		t.color(0, 0, 0);
		t.vertex(xm + r + 1, ym - h / 2 - 1, 0);
		t.vertex(xm - r - 1, ym - h / 2 - 1, 0);
		t.vertex(xm - r - 1, ym + h / 2 + 1, 0);
		t.vertex(xm + r + 1, ym + h / 2 + 1, 0);

		t.vertex(xm + h / 2 + 1, ym - r - 1, 0);
		t.vertex(xm - h / 2 - 1, ym - r - 1, 0);
		t.vertex(xm - h / 2 - 1, ym + r + 1, 0);
		t.vertex(xm + h / 2 + 1, ym + r + 1, 0);

		t.color(255, 255, 255);
		t.vertex(xm + r, ym - h / 2, 0);
		t.vertex(xm - r, ym - h / 2, 0);
		t.vertex(xm - r, ym + h / 2, 0);
		t.vertex(xm + r, ym + h / 2, 0);

		t.vertex(xm + h / 2, ym - r, 0);
		t.vertex(xm - h / 2, ym - r, 0);
		t.vertex(xm - h / 2, ym + r, 0);
		t.vertex(xm + h / 2, ym + r, 0);
		t.end();
		checkGLError("GUI: Draw crosshair");

		if (menu != null) {
			menu.render(Mouse.getX(), height - Mouse.getY() - 1);
		}
		checkGLError("Rendered gui");
		Display.update();
	}

	public void pick(float delta) {
		float xr = (float) (player.xRot * Math.PI * 2.0f / 360.0f);
		float yr = (float) (player.yRot * Math.PI * 2.0f / 360.0f);

		float xzMul = (float) -Math.cos(yr);

		float xa = (float) (-Math.sin(xr - Math.PI) * xzMul);
		float za = (float) (Math.cos(xr - Math.PI) * xzMul);
		float ya = (float) Math.sin(yr);

		float xp = this.player.xo + (this.player.x - this.player.xo) * delta;
		float yp = this.player.yo + (this.player.y - this.player.yo) * delta;
		float zp = this.player.zo + (this.player.z - this.player.zo) * delta;

		/*
		 * float dist = -1.0f; Entity eResult = null; float pickLength = 4.0f;
		 * for (int i = 0; i < pickLength * 256.0f; i++) { xp -= xa / 256.0f; yp
		 * -= ya / 256.0f; zp -= za / 256.0f; for (Entity e : level.entities) {
		 * AABB bb = e.aabb; if (xp >= bb.x0 && yp >= bb.y0 && zp >= bb.z0 && xp
		 * < bb.x1 && yp < bb.y1 && zp < bb.z1) { float xd = xp -
		 * this.player.xo; float yd = yp - this.player.yo; float zd = zp -
		 * this.player.zo; if (dist < 0.0f || dist * dist > xd * xd + yd * yd +
		 * zd * zd) { dist = xd * xd + yd * yd + zd * zd; eResult = e; } } } }
		 */

		/*
		 * Pick ray geht um einen Schritt mehr jedes Mal weiter in die Richtung
		 * wo der Spieler hinschaut. Wenn ein Block da ist wird die Schleife
		 * abgebrochen und die nächstgelegene Seite geprüft.
		 */
		synchronized (hitResultSynchronizer) {
			hitResult = null;
			for (int i = 0; i < 512 * 5; i++) {
				xp -= xa / 512.0;
				yp -= ya / 512.0;
				zp -= za / 512.0;
				int xi = (int) Math.floor(xp);
				int yi = (int) Math.floor(yp);
				int zi = (int) Math.floor(zp);

				int tile = level.getTile(xi, yi, zi);
				if (tile > 0 && Tile.tiles[tile].isPickable()) {
					double nearestDist = -1;
					int side = 0;

					for (int j = 0; j < 6; j++) {
						double yn = j < 2 ? j * 2 - 1 : 0;
						double xn = j >= 4 && j < 6 ? (j % 2) * 2 - 1 : 0;
						double zn = j >= 2 && j < 4 ? (j % 2) * 2 - 1 : 0;

						double xd = xp - xi - 0.5 - xn;
						double yd = yp - yi - 0.5 - yn;
						double zd = zp - zi - 0.5 - zn;

						double dist = xd * xd + yd * yd + zd * zd;
						if (nearestDist < 0 || dist < nearestDist) {
							nearestDist = dist;
							side = j;
						}
					}

					hitResult = new HitResult(xi, yi, zi, side);
					break;
				}
			}
		}
	}

	public void updateChunks() {
		FrustumCuller frustumCuller = FrustumCuller.getInstance();
		for (int i = 0; i < levelRenderer.chunkCache.length; i++) {
			Chunk c = levelRenderer.chunkCache[i];
			float viewDist = 256 / (1 << levelRenderer.viewDistance);
			if (c.distToPlayer(player) < viewDist * viewDist) {
				c.clip(frustumCuller);
				if (c.isVisible && !levelRenderer.loadedChunks.contains(c))
					levelRenderer.loadedChunks.add(c);
			}
			if (c.isDirty && c.isVisible) {
				c.update();
			}
			if (levelRenderer.loadedChunks.contains(c)
					&& (!c.isVisible || c.isDirty)) {
				levelRenderer.loadedChunks.remove(c);
			}
		}
		checkGLError("Update chunks");
	}

	public void renderShadowMap(float delta) {
		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, framebuffer);

		glColorMask(false, false, false, false);
		glViewport(0, 0, SHADOW_MAP_SIZE, SHADOW_MAP_SIZE);
		glClear(GL_DEPTH_BUFFER_BIT);

		glEnable(GL_POLYGON_OFFSET_FILL);
		glPolygonOffset(1.0f, 10.0f);

		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		gluPerspective(70, 1, 0.1f, 1000.0f);
		glMatrixMode(GL_MODELVIEW);

		glLoadIdentity();
		glRotatef(yLightRot, 1.0f, 0.0f, 0.0f);
		glRotatef(xLightRot, 0.0f, 1.0f, 0.0f);
		glTranslatef(-xLight, -yLight, -zLight);

		updateChunks();

		glGetFloat(GL_MODELVIEW_MATRIX, lightViewMatrix);
		glGetFloat(GL_PROJECTION_MATRIX, lightProjMatrix);

		glDisable(GL_CULL_FACE);
		renderSceneShadowMap(delta);

		glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
		glColorMask(true, true, true, true);
		glDisable(GL_POLYGON_OFFSET_FILL);
	}

	private void inverse(FloatBuffer target, FloatBuffer source, float delta) {
		target.put(source.get(0));
		target.put(source.get(4));
		target.put(source.get(8));
		target.put(0.0f);
		target.put(source.get(1));
		target.put(source.get(5));
		target.put(source.get(9));
		target.put(0.0f);
		target.put(source.get(2));
		target.put(source.get(6));
		target.put(source.get(10));
		target.put(0.0f);
		target.put(player.xo + (player.x - player.xo) * delta);
		target.put(player.yo + (player.y - player.yo) * delta);
		target.put(player.zo + (player.z - player.zo) * delta);
		target.put(1.0f);
	}

	public void renderSceneShadowMap(float delta) {
		levelRenderer.render(0);
		checkGLError("Rendered level");

		synchronized (level.entities) {
			for (int i = 0; i < level.entities.size(); i++) {
				Entity e = level.entities.get(i);
				e.render(delta);
			}
		}
		checkGLError("Rendered entities");

		this.particleEngine.render(player, delta);
		checkGLError("Rendered particles");

		// player.render(delta);
	}

	public void renderScene(float delta) {
		glEnable(GL_FOG);
		renderer.renderFog();
		levelRenderer.render(0);
		checkGLError("Rendered level");

		synchronized (level.entities) {
			for (int i = 0; i < level.entities.size(); i++) {
				Entity e = level.entities.get(i);
				e.render(delta);
			}
		}
		checkGLError("Rendered entities");

		this.particleEngine.render(player, delta);
		checkGLError("Rendered particles");

		glColorMask(false, false, false, false);
		this.levelRenderer.render(2);
		glColorMask(true, true, true, true);
		this.levelRenderer.render(2);
		glDisable(GL_FOG);

		basicColorShader.enable();
		clouds.render();
		glColor3f(253 / 255.0f, 184 / 255.0f, 19 / 255.0f);
		level.sun.render(xSunPos, ySunPos, zSunPos);
	}

	public void setProgressTitle(String text) {
		this.progressTitle = text;
		glClear(256);
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0.0, width, height, 0.0, 100.0, 300.0);
		glMatrixMode(GL_MODELVIEW);
		glLoadIdentity();
		glTranslatef(0.0f, 0.0f, -200.0f);
	}

	public void setProgressText(String text) {
		this.progressText = text;
	}

	public void setProgress(int progress) {
		loading = true;
		glClear(16640);

		Tesselator t = Tesselator.instance;
		glEnable(GL_TEXTURE_2D);
		glBindTexture(GL_TEXTURE_2D,
				TextureLoader.load("/terrain.png", GL_NEAREST));

		t.begin();
		t.color(0x606060);
		int tileSize = 32;
		int w = width / tileSize + 1;
		int h = height / tileSize + 1;
		Random random = new Random(100);
		for (int x = 0; x < w; x++)
			for (int y = 0; y < h; y++) {
				int xd = Math.abs(x - w / 2);
				int yd = Math.abs(y - h / 2);
				int br = 255 - (int) (Math.sqrt(xd * xd + yd * yd) * 15);
				int col = br << 16 | br << 8 | br;
				t.color(col);
				Tile tile = Tile.dirt;
				if (random.nextInt(3) == 0)
					tile = Tile.stone;
				float u0 = (tile.texIndex % 16) / 16.0f;
				float u1 = u0 + 1.0f / 16.0f;
				float v0 = (tile.texIndex / 16) / 16.0f;
				float v1 = v0 + 1.0f / 16.0f;
				t.vertexUV(x * tileSize, y * tileSize, 0.0f, u0, v0);
				t.vertexUV(x * tileSize + tileSize, y * tileSize, 0.0f, u1, v0);
				t.vertexUV(x * tileSize + tileSize, y * tileSize + tileSize,
						0.0f, u1, v1);
				t.vertexUV(x * tileSize, y * tileSize + tileSize, 0.0f, u0, v1);
			}
		t.end();

		glDisable(GL_TEXTURE_2D);
		if (progress >= 0) {
			int x = width / 2 - 100;
			int y = height / 2 + 32;

			t.begin();
			t.color(0x202020);
			t.vertex(x, y + 4, 0.0f);
			t.vertex(x, y + 6, 0.0f);
			t.vertex(x + 200, y + 6, 0.0f);
			t.vertex(x + 200, y + 4, 0.0f);
			t.color(0x808080);
			t.vertex(x, y, 0.0f);
			t.vertex(x, y + 4, 0.0f);
			t.vertex(x + 200, y + 4, 0.0f);
			t.vertex(x + 200, y, 0.0f);
			t.color(0x00CC00);
			t.vertex(x, y, 0.0f);
			t.vertex(x, y + 4, 0.0f);
			t.vertex(x + progress * 2, y + 4, 0.0f);
			t.vertex(x + progress * 2, y, 0.0f);
			t.end();
		}

		guiText.drawString(progressTitle,
				(width - TextRenderer.getTextLength(progressTitle)) / 2,
				height / 2 - 8 - 32, 0xffffff);
		guiText.drawString(progressText,
				(width - TextRenderer.getTextLength(progressText)) / 2,
				height / 2 - 8 + 16, 0xffffff);
		Display.update();
	}

	public void regenLevel() {
		this.generator.generate(level, 512, 128, 512);
		if (this.player != null) {
			this.player.resetPos();
		}
		if (levelRenderer != null)
			levelRenderer.loadedChunks.clear();
		synchronized (level.entities) {
			level.entities.clear();
		}
		/*
		 * for (int i = 0; i < 100; i++) { float x = level.random.nextFloat() *
		 * level.width; float z = level.random.nextFloat() * level.depth; float
		 * y = level.height; level.addEntity(new Human(level, textureLoader, x,
		 * y, z)); }
		 */
	}

	public void endLoading() {
		loading = false;
	}

	public static void main(String[] args) {
		game = new Cubeshaft(null, 854, 480, false);
		new PluginManager().load(new File("run"));
		if (game.startup()) {
			game.running = true;

			game.ticking.start();
			game.playerTicking.start();

			game.rendering.start();
			System.out.println("Started");

			game.running = false;

			game.playerTicking.join();
			game.ticking.join();
			game.stop();
		}
	}

}
