/*
 * Copyright 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.dom.client;

import com.google.gwt.dom.client.Style.BorderStyle;
import com.google.gwt.dom.client.Style.Clear;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontStyle;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.ListStyleType;
import com.google.gwt.dom.client.Style.OutlineStyle;
import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.dom.client.Style.TableLayout;
import com.google.gwt.dom.client.Style.TextAlign;
import com.google.gwt.dom.client.Style.TextDecoration;
import com.google.gwt.dom.client.Style.TextJustify;
import com.google.gwt.dom.client.Style.TextOverflow;
import com.google.gwt.dom.client.Style.TextTransform;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.VerticalAlign;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.dom.client.Style.WhiteSpace;

/**
 * An interface for {@link Style}.
 */
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

  String getFontStyle();

  void setFontStyle(FontStyle style);

  void clearFontStyle();

  String getColor();

  void setColor(String value);

  void clearColor();

  String getBackgroundColor();

  void setBackgroundColor(String value);

  void clearBackgroundColor();

  String getBackgroundImage();

  void setBackgroundImage(String value);

  void clearBackgroundImage();

  String getBottom();

  void setBottom(double value, Unit unit);

  void clearBottom();

  String getTop();

  void setTop(double value, Unit unit);

  void clearTop();

  String getLeft();

  void setLeft(double value, Unit unit);

  void clearLeft();

  String getRight();

  void setRight(double value, Unit unit);

  void clearRight();

  String getMargin();

  void setMargin(double value, Unit unit);

  void clearMargin();

  String getMarginTop();

  void setMarginTop(double value, Unit unit);

  void clearMarginTop();

  String getMarginBottom();

  void setMarginBottom(double value, Unit unit);

  void clearMarginBottom();

  String getMarginLeft();

  void setMarginLeft(double value, Unit unit);

  void clearMarginLeft();

  String getMarginRight();

  void setMarginRight(double value, Unit unit);

  void clearMarginRight();

  String getOverflow();

  void setOverflow(Overflow value);

  void clearOverflow();

  String getOverflowY();

  void setOverflowY(Overflow value);

  void clearOverflowY();

  String getOverflowX();

  void setOverflowX(Overflow value);

  void clearOverflowX();

  String getBorderColor();

  void setBorderColor(String value);

  void clearBorderColor();

  String getBorderStyle();

  void setBorderStyle(BorderStyle value);

  void clearBorderStyle();

  String getBorderWidth();

  void setBorderWidth(double value, Unit unit);

  void clearBorderWidth();

  String getDisplay();

  void setDisplay(Display value);

  void clearDisplay();

  String getClear();

  void setClear(Clear clear);

  void clearClear();

  String getProperty(String name);

  void setProperty(String name, String value);

  void setProperty(String name, double value, Unit unit);

  void setPropertyPx(String name, int value);

  void clearProperty(String name);

  String getPosition();

  void setPosition(Position position);

  void clearPosition();

  String getFontSize();

  void setFontSize(double value, Unit unit);

  void clearFontSize();

  String getFloat();

  void setFloat(Float value);

  void clearFloat();

  String getZIndex();

  void setZIndex(int value);

  void clearZIndex();

  String getVisibility();

  void setVisibility(Visibility value);

  void clearVisibility();

  String getCursor();

  void setCursor(Cursor value);

  void clearCursor();

  String getPadding();

  void setPadding(double value, Unit unit);

  void clearPadding();

  String getPaddingTop();

  void setPaddingTop(double value, Unit unit);

  void clearPaddingTop();

  String getPaddingBottom();

  void setPaddingBottom(double value, Unit unit);

  void clearPaddingBottom();

  String getPaddingLeft();

  void setPaddingLeft(double value, Unit unit);

  void clearPaddingLeft();

  String getPaddingRight();

  void setPaddingRight(double value, Unit unit);

  void clearPaddingRight();

  String getLineHeight();

  void setLineHeight(double value, Unit unit);

  void clearLineHeight();

  String getListStyleType();

  void setListStyleType(ListStyleType type);

  void clearListStyleType();

  String getOutlineColor();

  void setOutlineColor(String color);

  void clearOutlineColor();

  String getOutlineStyle();

  void setOutlineStyle(OutlineStyle style);

  void clearOutlineStyle();

  String getOutlineWidth();

  void setOutlineWidth(double value, Unit unit);

  void clearOutlineWidth();

  String getTableLayout();

  void setTableLayout(TableLayout tableLayout);

  void clearTableLayout();

  String getTextAlign();

  void setTextAlign(TextAlign textAlign);

  void clearTextAlign();

  String getTextDecoration();

  void setTextDecoration(TextDecoration textDecoration);

  void clearTextDecoration();

  String getTextIndent();

  void setTextIndent(double value, Unit unit);

  void clearTextIndent();

  String getTextJustify();

  void setTextJustify(TextJustify textJustify);

  void clearTextJustify();

  String getTextOverflow();

  void setTextOverflow(TextOverflow textOverflow);

  void clearTextOverflow();

  String getTextTransform();

  void setTextTransform(TextTransform textTransform);

  void clearTextTransform();

  String getWhiteSpace();

  void setWhiteSpace(WhiteSpace whiteSpace);

  void clearWhiteSpace();

  String getVerticalAlign();

  void setVerticalAlign(VerticalAlign verticalAlign);

  void setVerticalAlign(double value, Unit unit);

  void clearVerticalAlign();
}
