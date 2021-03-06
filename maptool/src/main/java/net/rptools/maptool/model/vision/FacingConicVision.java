/*
 * This software copyright by various authors including the RPTools.net
 * development team, and licensed under the LGPL Version 3 or, at your option,
 * any later version.
 *
 * Portions of this software were originally covered under the Apache Software
 * License, Version 1.1 or Version 2.0.
 *
 * See the file LICENSE elsewhere in this distribution for license details.
 */

package net.rptools.maptool.model.vision;

import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;

import net.rptools.maptool.model.GUID;
import net.rptools.maptool.model.Token;
import net.rptools.maptool.model.Vision;
import net.rptools.maptool.model.Zone;

public class FacingConicVision extends Vision {
	private Integer lastFacing;

	// DEPRECATED: here to support the serialization
	private transient GUID tokenGUID;

	public FacingConicVision() {
	}

	public FacingConicVision(int distance) {
		setDistance(distance);
	}

	@Override
	public Anchor getAnchor() {
		return Vision.Anchor.CENTER;
	}

	@Override
	public Area getArea(Zone zone, Token token) {
		if (token == null) {
			return null;
		}
		if (lastFacing == null || !lastFacing.equals(token.getFacing())) {
			flush();
			lastFacing = token.getFacing();
		}
		return super.getArea(zone, token);
	}

	@Override
	protected Area createArea(Zone zone, Token token) {
		if (token == null) {
			return null;
		}
		if (token.getFacing() == null) {
			token.setFacing(0);
		}
		// Start round
		int size = getDistance() * getZonePointsPerCell(zone) * 2;
		int half = size / 2;
		Area area = new Area(new Ellipse2D.Float(-half, -half, size, size));

		// Cut off the part that isn't in the cone
		area.subtract(new Area(new Rectangle(-100000, 1, 200000, 200000)));
		area.subtract(new Area(new Rectangle(-100000, -100000, 99999, 200000)));

		// Rotate
		int angle = (-token.getFacing() + 45);
		area.transform(AffineTransform.getRotateInstance(Math.toRadians(angle)));

		lastFacing = token.getFacing();
		return area;
	}

	@Override
	public String toString() {
		return "Conic Facing";
	}
}
