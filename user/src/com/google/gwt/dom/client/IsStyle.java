package com.google.gwt.dom.client;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;

/** An interface for {@link Style}. */
public interface IsStyle {

  String getOpacity();

  void setOpacity(double value);

  void clearOpacity();

  String getWidth();

  void setWidth(double value, Unit unit);

  void clearWidth();

  String getHeight();

  void setHeight(double value, Unit unit);

  void clearHeight();

  String getFontWeight();

  void setFontWeight(FontWeight value);

  void clearFontWeight();

  void setColor(String value);

  void clearColor();

  void setBackgroundColor(String value);

  String getBackgroundImage();

  void setBackgroundImage(String value);

  void clearBackgroundImage();

  void setLeft(double value, Unit unit);

  void clearLeft();

  void setTop(double value, Unit unit);

  void clearTop();

  void setBottom(double value, Unit unit);

  void clearBottom();

  void setRight(double value, Unit unit);

  void clearRight();

  void setMargin(double value, Unit unit);

  void clearMargin();

  void setMarginTop(double value, Unit unit);

  void setMarginBottom(double value, Unit unit);

  void setMarginLeft(double value, Unit unit);

  void setMarginRight(double value, Unit unit);

  void setOverflow(Overflow value);

  void setBorderColor(String value);

  void setBorderStyle(BorderStyle value);

  void setBorderWidth(double value, Unit unit);

  String getDisplay();

  void setDisplay(Display value);

  void clearDisplay();

  String getProperty(String name);

  void setProperty(String name, String value);

  void setProperty(String name, double value, Unit unit);

  void setPropertyPx(String name, int value);

  String getPosition();

  void setPosition(Position position);

  String getFontSize();

  void setFontSize(double value, Unit unit);

  void setFloat(Float value);

  void clearFloat();

  void setZIndex(int value);

  void clearZIndex();

  void setVisibility(Visibility value);

  void clearVisibility();

  void setCursor(Cursor value);

  void clearCursor();

  void setPadding(double value, Unit unit);

  void clearPadding();
}
