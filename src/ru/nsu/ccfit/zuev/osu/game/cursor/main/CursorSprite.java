package ru.nsu.ccfit.zuev.osu.game.cursor.main;

import org.anddev.andengine.entity.modifier.ParallelEntityModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.opengl.texture.region.TextureRegion;

import ru.nsu.ccfit.zuev.osu.Config;
import ru.nsu.ccfit.zuev.osu.game.ISliderListener;

public class CursorSprite extends Sprite implements ISliderListener {
	public final float baseSize = 2f * Config.getCursorSize();
	private final float clickAnimationTime = 0.5f / 2f;
	private ParallelEntityModifier previousClickModifier;

	public CursorSprite(float pX, float pY, TextureRegion pTextureRegion) {
		super(pX, pY, pTextureRegion);
		setScale(baseSize);
	}

	public ScaleModifier clickInModifier() {
		return new ScaleModifier(clickAnimationTime, getScaleX(), baseSize * 1.25f);
	}

	public ScaleModifier clickOutModifier() {
		return new ScaleModifier(clickAnimationTime, getScaleX(), baseSize);
	}

	public void handleClick() {
		if (previousClickModifier != null) {
			unregisterEntityModifier(previousClickModifier);
			setScale(baseSize);
		}
		registerEntityModifier(
    previousClickModifier = new ParallelEntityModifier(
    		new SequenceEntityModifier(clickInModifier(), clickOutModifier())
    )
		);
	}

	public void update(float pSecondsElapsed, boolean isShowing) {
		setVisible(isShowing);

		if (getScaleX() > 2f) {
			setScale(Math.max(baseSize, this.getScaleX() - (baseSize * 0.75f) * pSecondsElapsed));
		}
	}

	@Override
	public void onSliderStart() {

	}

	@Override
	public void onSliderTracking() {
		registerEntityModifier(clickInModifier());
	}

	@Override
	public void onSliderEnd() {
		registerEntityModifier(clickOutModifier());
	}
}
