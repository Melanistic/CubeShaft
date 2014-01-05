/*
 * This file is part of Cubeshaft
 * Copyright Naronco 2013
 * Sharing and using is only allowed with written permission of Naronco
 */

package com.naronco.cubeshaft.model;

public class SkeletonModel extends Model {
	public Cube head;
	public Cube body;
	public Cube rightArm;
	public Cube leftArm;
	public Cube rightLeg;
	public Cube leftLeg;

	public SkeletonModel() {
		this.head = new Cube(0, 0);
		this.head.setBounds(-4.0F, -8.0F, -4.0F, 8, 8, 8);
		this.body = new Cube(16, 16);
		this.body.setBounds(-4.0F, 0.0F, -2.0F, 8, 12, 4);
		this.rightArm = new Cube(40, 16);
		this.rightArm.setBounds(-3.0F, -2.0F, -2.0F, 2, 12, 2);
		this.rightArm.setPos(-5.0F, 2.0F, 0.0F);
		this.leftArm = new Cube(40, 16);
		this.leftArm.setBounds(-1.0F, -2.0F, -2.0F, 2, 12, 2);
		this.leftArm.setPos(4.0F, 2.0F, 0.0F);
		this.rightLeg = new Cube(0, 16);
		this.rightLeg.setBounds(-2.0F, 0.0F, -2.0F, 2, 12, 2);
		this.rightLeg.setPos(-2.0F, 12.0F, 0.0F);
		this.leftLeg = new Cube(0, 16);
		this.leftLeg.setBounds(-2.0F, 0.0F, -2.0F, 2, 12, 2);
		this.leftLeg.setPos(2.0F, 12.0F, 0.0F);
	}
}
