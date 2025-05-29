package com.bestbudz.rendering;

import com.bestbudz.rendering.model.Class33;
import com.bestbudz.rendering.model.Model;
import com.bestbudz.util.NodeSub;

public class Animable extends NodeSub {

  public int modelHeight;
  public Class33[] aClass33Array1425;

  protected Animable() {
    modelHeight = 1000;
  }

  public void method443(int i, int j, int k, int l, int i1, int j1, int k1, int l1, int i2) {
    Model model = getFinalRenderedModel();
    if (model != null) {
      modelHeight = model.modelHeight;
      model.method443(i, j, k, l, i1, j1, k1, l1, i2);
    }
  }


  public Model getFinalRenderedModel() {
    return null;
  }
}
