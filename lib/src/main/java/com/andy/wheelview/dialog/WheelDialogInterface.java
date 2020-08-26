package com.andy.wheelview.dialog;

import com.andy.wheelview.base.IWheel;

public interface WheelDialogInterface<T extends IWheel> {

    boolean onClick(int witch, int selectedIndex, T item);
}