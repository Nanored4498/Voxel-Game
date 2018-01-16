package fr.coudert.rendering;

import fr.coudert.game.scenes.Game;
import fr.coudert.maths.Vec3;

public class FrustumCulling {

	private static float l, r, b, t;
	private static Vec3 nearTopRight, nearBotLeft;
	private static Vec3 leftNormal, rightNormal, topNormal, botNormal;
	private static Vec3 camPos;
	private static int time = 0;

	public static void update(Game game) {
		time ++;
		if(time != 2)
			return;
		float farH = (float) (Math.tan(Math.toRadians(Camera.fov / 2.0f)) * Camera.far), farW = farH * Camera.aspect;
		float nearH = (float) (Math.tan(Math.toRadians(Camera.fov / 2.0f)) * Camera.near), nearW = nearH * Camera.aspect;
		camPos = game.getPlayer().getEyesPos();
		Vec3 camDir = game.getPlayer().dir;
		Vec3 camLeft = game.getPlayer().getLeft().normalized(), camUp = game.getPlayer().getUp();
		Vec3 farCenter = camPos.copy().add(camDir.copy().mul(Camera.far));
		Vec3 nearCenter = camPos.copy().add(camDir.copy().mul(Camera.near));
		nearTopRight = nearCenter.copy().add(camLeft.copy().mul(-nearW)).add(camUp.copy().mul(nearH));
		nearBotLeft = nearCenter.add(camLeft.copy().mul(nearW)).add(camUp.copy().mul(-nearH));
		Vec3 tr = farCenter.copy().add(camLeft.copy().mul(-farW)).add(camUp.copy().mul(farH)).sub(nearTopRight);
		Vec3 bl = farCenter.add(camLeft.copy().mul(farW)).add(camUp.copy().mul(-farH)).sub(nearBotLeft);
		rightNormal = tr.copy().cross(camUp).normalized();
		leftNormal = camUp.cross(bl).normalized();
		topNormal = tr.cross(camLeft).normalized();
		botNormal = camLeft.cross(bl).normalized();
		l = leftNormal.copy().dot(nearBotLeft);
		r = rightNormal.copy().dot(nearTopRight);
		t = topNormal.copy().dot(nearTopRight);
		b = botNormal.copy().dot(nearBotLeft);
		time = 0;
	}

	public static boolean isInViewFrustum(Vec3 pos, float radius) {
		if(leftNormal == null)
			return true;
		if(leftNormal.dot(pos) > l+radius || rightNormal.dot(pos) > r+radius ||
				topNormal.dot(pos) > t+radius || botNormal.dot(pos) > b+radius)
			return false;
		if(camPos.copy().sub(pos).length() > Camera.far*0.58f+radius)
			return false;
		return true;
	}

}