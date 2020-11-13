/********************************************************************************
 * Copyright (c) 2020 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
package org.eclipse.glsp.example.binalyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "template")
public class GTemplate {

   @XmlTransient
   public GTemplate parent;

   List<GTemplate> children = new ArrayList<>();

   String id;
   String name;
   String taskType;
   String type;
   String layout;
   Double x, y;
   Double width, height;
   Integer count;
   Integer offset;
   Integer size;
   Integer paddingBefore;
   Integer paddingAfter;
   Integer boundary;
   String signature;
   String hint;

   public GTemplate() {
      super();
   }

   public GTemplate(final List<GTemplate> children) {
      super();
      this.children = children;
   }

   @XmlElement(name = "template")
   public List<GTemplate> getChildren() { return children; }

   public void setChildren(final List<GTemplate> children) { this.children = children; }

   @XmlAttribute(name = "id", namespace = "https://schemas.denisvasilik.com/glsp")
   public String getId() { return id; }

   public void setId(final String id) { this.id = id; }

   @XmlAttribute(name = "name")
   public String getName() { return name; }

   public void setName(final String name) { this.name = name; }

   @XmlAttribute(name = "taskType", namespace = "https://schemas.denisvasilik.com/glsp")
   public String getTaskType() { return taskType; }

   public void setTaskType(final String taskType) { this.taskType = taskType; }

   @XmlAttribute(name = "type", namespace = "https://schemas.denisvasilik.com/glsp")
   public String getType() { return type; }

   public void setType(final String type) { this.type = type; }

   @XmlAttribute(name = "layout", namespace = "https://schemas.denisvasilik.com/glsp")
   public String getLayout() { return layout; }

   public void setLayout(final String layout) { this.layout = layout; }

   @XmlAttribute(name = "x", namespace = "https://schemas.denisvasilik.com/glsp")
   public Double getX() { return x; }

   public void setX(final Double x) { this.x = x; }

   @XmlAttribute(name = "y", namespace = "https://schemas.denisvasilik.com/glsp")
   public Double getY() { return y; }

   public void setY(final Double y) { this.y = y; }

   @XmlAttribute(name = "width", namespace = "https://schemas.denisvasilik.com/glsp")
   public Double getWidth() { return width; }

   public void setWidth(final Double width) { this.width = width; }

   @XmlAttribute(name = "height", namespace = "https://schemas.denisvasilik.com/glsp")
   public Double getHeight() { return height; }

   public void setHeight(final Double height) { this.height = height; }

   @XmlAttribute(name = "count")
   public Integer getCount() { return count; }

   public void setCount(final Integer count) { this.count = count; }

   @XmlAttribute(name = "offset")
   public Integer getOffset() { return offset; }

   public void setOffset(final Integer offset) { this.offset = offset; }

   @XmlAttribute(name = "size")
   public Integer getSize() { return size; }

   public void setSize(final Integer size) { this.size = size; }

   @XmlAttribute(name = "padding-before")
   public Integer getPaddingBefore() { return paddingBefore; }

   public void setPaddingBefore(final Integer paddingBefore) { this.paddingBefore = paddingBefore; }

   @XmlAttribute(name = "padding-after")
   public Integer getPaddingAfter() { return paddingAfter; }

   public void setPaddingAfter(final Integer paddingAfter) { this.paddingAfter = paddingAfter; }

   @XmlAttribute(name = "boundary")
   public Integer getBoundary() { return boundary; }

   public void setBoundary(final Integer boundary) { this.boundary = boundary; }

   @XmlAttribute(name = "signature")
   public String getSignature() { return signature; }

   public void setSignature(final String signature) { this.signature = signature; }

   @XmlAttribute(name = "hint")
   public String getHint() { return hint; }

   public void setHint(final String hint) { this.hint = hint; }

   public void setDefaultValues() {
      if (id == null) {
         id = "";
      }
      if (taskType == null) {
         taskType = "manual";
      }
      if (type == null) {
         type = "task:manual";
      }
      if (x == null) {
         x = 0.0;
      }
      if (y == null) {
         y = 0.0;
      }
      if (width == null) {
         width = 50.0;
      }
      if (height == null) {
         height = 50.0;
      }
      if (layout == null) {
         layout = "vbox";
      }
   }

   public static Stream<GTemplate> leaves(final GTemplate template) {
      if (template.getChildren().size() > 0) {
         return template.getChildren().stream().flatMap(GTemplate::leaves);
      }
      return Stream.of(template);
   }

   public Stream<GTemplate> flattened() {
      return Stream.concat(Stream.of(this), this.children.stream().flatMap(GTemplate::flattened));
   }
}
