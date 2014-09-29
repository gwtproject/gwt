/*
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
package com.google.gwt.user.client.ui;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.OptGroupElement;
import com.google.gwt.dom.client.OptionElement;
import com.google.gwt.dom.client.SelectElement;
import com.google.gwt.i18n.client.HasDirection.Direction;

/**
 * A widget that displays a list of options for the user and the ability to create
 * &#60;optgroup&#62; in a drop-down list.
 * <p>
 * <h3>CSS Style Rules</h3>
 * <ul class='css'>
 * <li>.gwt-OptGroupListBox { }</li>
 * </ul>
 * </p>
 * 
 * @author araujo921
 * 
 */
public class OptGroupListBox extends ListBox {

  private static final String OPT_GROUP = "OPTGROUP";

  public OptGroupListBox() {
    setStyleName("gwt-OptGroupListBox");
  }

  @Override
  public void clear() {
    // super.clear();
    NodeList<Element> nodeList = getSelectElement().getElementsByTagName(OPT_GROUP);
    for (int i = nodeList.getLength() - 1; i > -1; i--)
      nodeList.getItem(i).removeFromParent();
  }

  /**
   * @return the number of groups
   */
  public int getGroupCount() {
    return getSelectElement().getElementsByTagName(OPT_GROUP).getLength();
  }

  /**
   * Adds a new group.
   * 
   * @param label group name.
   * @param items items of the group.
   */
  public void addGroup(String label, String... items) {
    addGroup(label, items, items);
  }

  /**
   * @param label
   * @param disabled
   * @param items
   */
  public void addGroup(String label, boolean disabled, String... items) {
    insertGroup(label, items, items, null, -1, disabled);
  }

  /**
   * @param label
   * @param items
   * @param values
   */
  public void addGroup(String label, String[] items, String[] values) {
    insertGroup(label, items, values, null, -1, false);
  }

  /**
   * @param label
   * @param items
   * @param dir
   */
  public void addGroup(String label, String[] items, Direction dir) {
    insertGroup(label, items, items, dir, -1, false);
  }

  /**
   * @param index
   */
  public void removeGroup(int index) {
    checkIndex(index);
    getSelectElement().getChild(index).removeFromParent();
  }

  /**
   * @param label
   */
  public void removeGroup(String label) {
    checkLabel(label);
    NodeList<Element> nodeList = getSelectElement().getElementsByTagName(OPT_GROUP);
    for (int i = 0; i < nodeList.getLength(); i++) {
      OptGroupElement optGroupElement = (OptGroupElement) nodeList.getItem(i);
      // let's remove extra space...
      if (optGroupElement.getLabel().trim().equalsIgnoreCase(label.trim())) {
        nodeList.getItem(i).removeFromParent();
        return;
      }
    }
  }

  /**
   * @param index
   * @param newLabel
   */
  public void setGroup(int index, String newLabel) {
    checkIndex(index);
    checkLabel(newLabel);
    ((OptGroupElement) getSelectElement().getChild(index)).setLabel(newLabel);
  }

  /**
   * @param oldLabel
   * @param newLabel
   */
  public void setGroup(String oldLabel, String newLabel) {
    checkLabel(oldLabel);
    checkLabel(newLabel);
    NodeList<Element> nodeList = getSelectElement().getElementsByTagName(OPT_GROUP);
    for (int i = 0; i < nodeList.getLength(); i++)
      if (((OptGroupElement) nodeList.getItem(i)).getLabel().equalsIgnoreCase(oldLabel))
        ((OptGroupElement) nodeList.getItem(i)).setLabel(newLabel);
  }

  /**
   * @param label
   * @param items
   * @param values
   * @param dir
   * @param index
   */
  public void insertGroup(String label, String[] items, String[] values, Direction dir, int index,
      boolean disabled) {
    checkLabel(label);
    OptGroupElement optGroupElement = Document.get().createOptGroupElement();
    optGroupElement.setLabel(label);
    optGroupElement.setDisabled(disabled);
    for (int i = 0; i < items.length && items.length <= values.length; i++) {
      OptionElement optionElement = Document.get().createOptionElement();
      setOptionText(optionElement, items[i], dir);
      optionElement.setValue(values[i]);
      optGroupElement.appendChild(optionElement);
    }
    SelectElement selectElement = getElement().cast();
    index = index < 0 || index > getItemCount() ? index = getItemCount() : index;
    if (index == getItemCount())
      selectElement.insertAfter(optGroupElement, null);
    else {
      OptGroupElement before = selectElement.getChild(index).cast();
      selectElement.insertBefore(optGroupElement, before);
    }
  }

  private final SelectElement getSelectElement() {
    return getElement().cast();
  }

  private final void checkIndex(int index) {
    assert index > -1 && index < getSelectElement().getChildCount() : new IndexOutOfBoundsException();
  }

  private final void checkLabel(String label) {
    assert label != null && !label.isEmpty() : new Exception("label is mandatory");
  }
}
