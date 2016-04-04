package sQuire_editor;

import org.fxmisc.flowless.Virtualized;
import org.fxmisc.richtext.CodeArea;
import org.reactfx.value.Val;
import org.reactfx.value.Var;

public class VirtualizedCodeArea extends CodeArea implements Virtualized{

	@Override
	public Var<Double> estimatedScrollXProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Var<Double> estimatedScrollYProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Val<Double> totalHeightEstimateProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Val<Double> totalWidthEstimateProperty() {
		// TODO Auto-generated method stub
		return null;
	}

}
