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

  /**
   * See {@link Style#getOpacity()}.
   */
  String getOpacity();

  /**
   * See {@link Style#setOpacity(double)}.
   */
  void setOpacity(double value);

  /**
   * See {@link Style#clearOpacity()}.
   */
  void clearOpacity();

  /**
   * See {@link Style#getWidth()}.
   */
  String getWidth();

  /**
   * See {@link Style#setWidth(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setWidth(double value, Unit unit);

  /**
   * See {@link Style#clearWidth()}.
   */
  void clearWidth();

  /**
   * See {@link Style#getHeight()}.
   */
  String getHeight();

  /**
   * See {@link Style#setHeight(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setHeight(double value, Unit unit);

  /**
   * See {@link Style#clearHeight()}.
   */
  void clearHeight();

  /**
   * See {@link Style#getFontWeight()}.
   */
  String getFontWeight();

  /**
   * See {@link Style#setFontWeight(com.google.gwt.dom.client.Style.FontWeight)}.
   */
  void setFontWeight(FontWeight value);

  /**
   * See {@link Style#clearFontWeight()}.
   */
  void clearFontWeight();

  /**
   * See {@link Style#getFontStyle()}.
   */
  String getFontStyle();

  /**
   * See {@link Style#setFontStyle(com.google.gwt.dom.client.Style.FontStyle)}.
   */
  void setFontStyle(FontStyle style);

  /**
   * See {@link Style#clearFontStyle()}.
   */
  void clearFontStyle();

  /**
   * See {@link Style#getColor()}.
   */
  String getColor();

  /**
   * See {@link Style#setColor(java.lang.String)}.
   */
  void setColor(String value);

  /**
   * See {@link Style#clearColor()}.
   */
  void clearColor();

  /**
   * See {@link Style#getBackgroundColor()}.
   */
  String getBackgroundColor();

  /**
   * See {@link Style#setBackgroundColor(java.lang.String)}.
   */
  void setBackgroundColor(String value);

  /**
   * See {@link Style#clearBackgroundColor()}.
   */
  void clearBackgroundColor();

  /**
   * See {@link Style#getBackgroundImage()}.
   */
  String getBackgroundImage();

  /**
   * See {@link Style#setBackgroundImage(java.lang.String)}.
   */
  void setBackgroundImage(String value);

  /**
   * See {@link Style#clearBackgroundImage()}.
   */
  void clearBackgroundImage();

  /**
   * See {@link Style#getBottom()}.
   */
  String getBottom();

  /**
   * See {@link Style#setBottom(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setBottom(double value, Unit unit);

  /**
   * See {@link Style#clearBottom()}.
   */
  void clearBottom();

  /**
   * See {@link Style#getTop()}.
   */
  String getTop();

  /**
   * See {@link Style#setTop(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setTop(double value, Unit unit);

  /**
   * See {@link Style#clearTop()}.
   */
  void clearTop();

  /**
   * See {@link Style#getLeft()}.
   */
  String getLeft();

  /**
   * See {@link Style#setLeft(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setLeft(double value, Unit unit);

  /**
   * See {@link Style#clearLeft()}.
   */
  void clearLeft();

  /**
   * See {@link Style#getRight()}.
   */
  String getRight();

  /**
   * See {@link Style#setRight(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setRight(double value, Unit unit);

  /**
   * See {@link Style#clearRight()}.
   */
  void clearRight();

  /**
   * See {@link Style#getMargin()}.
   */
  String getMargin();

  /**
   * See {@link Style#setMargin(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setMargin(double value, Unit unit);

  /**
   * See {@link Style#clearMargin()}.
   */
  void clearMargin();

  /**
   * See {@link Style#getMarginTop()}.
   */
  String getMarginTop();

  /**
   * See {@link Style#setMarginTop(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setMarginTop(double value, Unit unit);

  /**
   * See {@link Style#clearMarginTop()}.
   */
  void clearMarginTop();

  /**
   * See {@link Style#getMarginBottom()}.
   */
  String getMarginBottom();

  /**
   * See {@link Style#setMarginBottom(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setMarginBottom(double value, Unit unit);

  /**
   * See {@link Style#clearMarginBottom()}.
   */
  void clearMarginBottom();

  /**
   * See {@link Style#getMarginLeft()}.
   */
  String getMarginLeft();

  /**
   * See {@link Style#setMarginLeft(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setMarginLeft(double value, Unit unit);

  /**
   * See {@link Style#clearMarginLeft()}.
   */
  void clearMarginLeft();

  /**
   * See {@link Style#getMarginRight()}.
   */
  String getMarginRight();

  /**
   * See {@link Style#setMarginRight(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setMarginRight(double value, Unit unit);

  /**
   * See {@link Style#clearMarginRight()}.
   */
  void clearMarginRight();

  /**
   * See {@link Style#getOverflow()}.
   */
  String getOverflow();

  /**
   * See {@link Style#setOverflow(com.google.gwt.dom.client.Style.Overflow)}.
   */
  void setOverflow(Overflow value);

  /**
   * See {@link Style#clearOverflow()}.
   */
  void clearOverflow();

  /**
   * See {@link Style#getOverflowY()}.
   */
  String getOverflowY();

  /**
   * See {@link Style#setOverflowY(com.google.gwt.dom.client.Style.Overflow)}.
   */
  void setOverflowY(Overflow value);

  /**
   * See {@link Style#clearOverflowY()}.
   */
  void clearOverflowY();

  /**
   * See {@link Style#getOverflowX()}.
   */
  String getOverflowX();

  /**
   * See {@link Style#setOverflowX(com.google.gwt.dom.client.Style.Overflow)}.
   */
  void setOverflowX(Overflow value);

  /**
   * See {@link Style#clearOverflowX()}.
   */
  void clearOverflowX();

  /**
   * See {@link Style#getBorderColor()}.
   */
  String getBorderColor();

  /**
   * See {@link Style#setBorderColor(java.lang.String)}.
   */
  void setBorderColor(String value);

  /**
   * See {@link Style#clearBorderColor()}.
   */
  void clearBorderColor();

  /**
   * See {@link Style#getBorderStyle()}.
   */
  String getBorderStyle();

  /**
   * See {@link Style#setBorderStyle(com.google.gwt.dom.client.Style.BorderStyle)}.
   */
  void setBorderStyle(BorderStyle value);

  /**
   * See {@link Style#clearBorderStyle()}.
   */
  void clearBorderStyle();

  /**
   * See {@link Style#getBorderWidth()}.
   */
  String getBorderWidth();

  /**
   * See {@link Style#setBorderWidth(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setBorderWidth(double value, Unit unit);

  /**
   * See {@link Style#clearBorderWidth()}.
   */
  void clearBorderWidth();

  /**
   * See {@link Style#getDisplay()}.
   */
  String getDisplay();

  /**
   * See {@link Style#setDisplay(com.google.gwt.dom.client.Style.Display)}.
   */
  void setDisplay(Display value);

  /**
   * See {@link Style#clearDisplay()}.
   */
  void clearDisplay();

  /**
   * See {@link Style#getClear()}.
   */
  String getClear();

  /**
   * See {@link Style#setClear(com.google.gwt.dom.client.Style.Clear)}.
   */
  void setClear(Clear clear);

  /**
   * See {@link Style#clearClear()}.
   */
  void clearClear();

  /**
   * See {@link Style#getProperty(java.lang.String)}.
   */
  String getProperty(String name);

  /**
   * See {@link Style#setProperty(java.lang.String, java.lang.String)}.
   */
  void setProperty(String name, String value);

  /**
   * See {@link Style#setProperty(java.lang.String, double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setProperty(String name, double value, Unit unit);

  /**
   * See {@link Style#setPropertyPx(java.lang.String, int)}.
   */
  void setPropertyPx(String name, int value);

  /**
   * See {@link Style#clearProperty(java.lang.String)}.
   */
  void clearProperty(String name);

  /**
   * See {@link Style#getPosition()}.
   */
  String getPosition();

  /**
   * See {@link Style#setPosition(com.google.gwt.dom.client.Style.Position)}.
   */
  void setPosition(Position position);

  /**
   * See {@link Style#clearPosition()}.
   */
  void clearPosition();

  /**
   * See {@link Style#getFontSize()}.
   */
  String getFontSize();

  /**
   * See {@link Style#setFontSize(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setFontSize(double value, Unit unit);

  /**
   * See {@link Style#clearFontSize()}.
   */
  void clearFontSize();

  /**
   * See {@link Style#getFloat()}.
   */
  String getFloat();

  /**
   * See {@link Style#setFloat(com.google.gwt.dom.client.Style.Float)}.
   */
  void setFloat(Float value);

  /**
   * See {@link Style#clearFloat()}.
   */
  void clearFloat();

  /**
   * See {@link Style#getZIndex()}.
   */
  String getZIndex();

  /**
   * See {@link Style#setZIndex(int)}.
   */
  void setZIndex(int value);

  /**
   * See {@link Style#clearZIndex()}.
   */
  void clearZIndex();

  /**
   * See {@link Style#getVisibility()}.
   */
  String getVisibility();

  /**
   * See {@link Style#setVisibility(com.google.gwt.dom.client.Style.Visibility)}.
   */
  void setVisibility(Visibility value);

  /**
   * See {@link Style#clearVisibility()}.
   */
  void clearVisibility();

  /**
   * See {@link Style#getCursor()}.
   */
  String getCursor();

  /**
   * See {@link Style#setCursor(com.google.gwt.dom.client.Style.Cursor)}.
   */
  void setCursor(Cursor value);

  /**
   * See {@link Style#clearCursor()}.
   */
  void clearCursor();

  /**
   * See {@link Style#getPadding()}.
   */
  String getPadding();

  /**
   * See {@link Style#setPadding(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setPadding(double value, Unit unit);

  /**
   * See {@link Style#clearPadding()}.
   */
  void clearPadding();

  /**
   * See {@link Style#getPaddingTop()}.
   */
  String getPaddingTop();

  /**
   * See {@link Style#setPaddingTop(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setPaddingTop(double value, Unit unit);

  /**
   * See {@link Style#clearPaddingTop()}.
   */
  void clearPaddingTop();

  /**
   * See {@link Style#getPaddingBottom()}.
   */
  String getPaddingBottom();

  /**
   * See {@link Style#setPaddingBottom(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setPaddingBottom(double value, Unit unit);

  /**
   * See {@link Style#clearPaddingBottom()}.
   */
  void clearPaddingBottom();

  /**
   * See {@link Style#getPaddingLeft()}.
   */
  String getPaddingLeft();

  /**
   * See {@link Style#setPaddingLeft(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setPaddingLeft(double value, Unit unit);

  /**
   * See {@link Style#clearPaddingLeft()}.
   */
  void clearPaddingLeft();

  /**
   * See {@link Style#getPaddingRight()}.
   */
  String getPaddingRight();

  /**
   * See {@link Style#setPaddingRight(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setPaddingRight(double value, Unit unit);

  /**
   * See {@link Style#clearPaddingRight()}.
   */
  void clearPaddingRight();

  /**
   * See {@link Style#getLineHeight()}.
   */
  String getLineHeight();

  /**
   * See {@link Style#setLineHeight(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setLineHeight(double value, Unit unit);

  /**
   * See {@link Style#clearLineHeight()}.
   */
  void clearLineHeight();

  /**
   * See {@link Style#getListStyleType()}.
   */
  String getListStyleType();

  /**
   * See {@link Style#setListStyleType(com.google.gwt.dom.client.Style.ListStyleType)}.
   */
  void setListStyleType(ListStyleType type);

  /**
   * See {@link Style#clearListStyleType()}.
   */
  void clearListStyleType();

  /**
   * See {@link Style#getOutlineColor()}.
   */
  String getOutlineColor();

  /**
   * See {@link Style#setOutlineColor(java.lang.String)}.
   */
  void setOutlineColor(String color);

  /**
   * See {@link Style#clearOutlineColor()}.
   */
  void clearOutlineColor();

  /**
   * See {@link Style#getOutlineStyle()}.
   */
  String getOutlineStyle();

  /**
   * See {@link Style#setOutlineStyle(com.google.gwt.dom.client.Style.OutlineStyle)}.
   */
  void setOutlineStyle(OutlineStyle style);

  /**
   * See {@link Style#clearOutlineStyle()}.
   */
  void clearOutlineStyle();

  /**
   * See {@link Style#getOutlineWidth()}.
   */
  String getOutlineWidth();

  /**
   * See {@link Style#setOutlineWidth(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setOutlineWidth(double value, Unit unit);

  /**
   * See {@link Style#clearOutlineWidth()}.
   */
  void clearOutlineWidth();

  /**
   * See {@link Style#getTableLayout()}.
   */
  String getTableLayout();

  /**
   * See {@link Style#setTableLayout(com.google.gwt.dom.client.Style.TableLayout)}.
   */
  void setTableLayout(TableLayout tableLayout);

  /**
   * See {@link Style#clearTableLayout()}.
   */
  void clearTableLayout();

  /**
   * See {@link Style#getTextAlign()}.
   */
  String getTextAlign();

  /**
   * See {@link Style#setTextAlign(com.google.gwt.dom.client.Style.TextAlign)}.
   */
  void setTextAlign(TextAlign textAlign);

  /**
   * See {@link Style#clearTextAlign()}.
   */
  void clearTextAlign();

  /**
   * See {@link Style#getTextDecoration()}.
   */
  String getTextDecoration();

  /**
   * See {@link Style#setTextDecoration(com.google.gwt.dom.client.Style.TextDecoration)}.
   */
  void setTextDecoration(TextDecoration textDecoration);

  /**
   * See {@link Style#clearTextDecoration()}.
   */
  void clearTextDecoration();

  /**
   * See {@link Style#getTextIndent()}.
   */
  String getTextIndent();

  /**
   * See {@link Style#setTextIndent(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setTextIndent(double value, Unit unit);

  /**
   * See {@link Style#clearTextIndent()}.
   */
  void clearTextIndent();

  /**
   * See {@link Style#getTextJustify()}.
   */
  String getTextJustify();

  /**
   * See {@link Style#setTextJustify(com.google.gwt.dom.client.Style.TextJustify)}.
   */
  void setTextJustify(TextJustify textJustify);

  /**
   * See {@link Style#clearTextJustify()}.
   */
  void clearTextJustify();

  /**
   * See {@link Style#getTextOverflow()}.
   */
  String getTextOverflow();

  /**
   * See {@link Style#setTextOverflow(com.google.gwt.dom.client.Style.TextOverflow)}.
   */
  void setTextOverflow(TextOverflow textOverflow);

  /**
   * See {@link Style#clearTextOverflow()}.
   */
  void clearTextOverflow();

  /**
   * See {@link Style#getTextTransform()}.
   */
  String getTextTransform();

  /**
   * See {@link Style#setTextTransform(com.google.gwt.dom.client.Style.TextTransform)}.
   */
  void setTextTransform(TextTransform textTransform);

  /**
   * See {@link Style#clearTextTransform()}.
   */
  void clearTextTransform();

  /**
   * See {@link Style#getWhiteSpace()}.
   */
  String getWhiteSpace();

  /**
   * See {@link Style#setWhiteSpace(com.google.gwt.dom.client.Style.WhiteSpace)}.
   */
  void setWhiteSpace(WhiteSpace whiteSpace);

  /**
   * See {@link Style#clearWhiteSpace()}.
   */
  void clearWhiteSpace();

  /**
   * See {@link Style#getVerticalAlign()}.
   */
  String getVerticalAlign();

  /**
   * See {@link Style#setVerticalAlign(com.google.gwt.dom.client.Style.VerticalAlign)}.
   */
  void setVerticalAlign(VerticalAlign verticalAlign);

  /**
   * See {@link Style#setVerticalAlign(double, com.google.gwt.dom.client.Style.Unit)}.
   */
  void setVerticalAlign(double value, Unit unit);

  /**
   * See {@link Style#clearVerticalAlign()}.
   */
  void clearVerticalAlign();
}
