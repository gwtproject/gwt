/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.gwt.core.ext.soyc.coderef;

import com.google.gwt.thirdparty.guava.common.collect.Lists;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

/**
 * It contains all packages and they reference to classes {@link ClassDescriptor}.
 *
 * @author ocallau@google.com (Oscar Callau)
 */
public class PackageDescriptor {

  public static final String DEFAULT_PKG = "<default>";

  public static PackageDescriptor newFrom(Map<String, ClassDescriptor> codeGraph) {
    PackageDescriptor pkg = newFrom(codeGraph.values(), DEFAULT_PKG, "");
    pkg.condense();
    return pkg;
  }

  private static PackageDescriptor newFrom(Collection<ClassDescriptor> clss, String nm, String lnm) {
    PackageDescriptor pkg = new PackageDescriptor(nm,lnm);
    pkg.computeSubPackages(clss);
    return pkg;
  }

  protected ArrayList<PackageDescriptor> packages = Lists.newArrayList();
  protected ArrayList<ClassDescriptor> classes = Lists.newArrayList();
  protected String name = DEFAULT_PKG;
  protected String longName = "";

  public PackageDescriptor(String nm, String longnm) {
    name = nm;
    longName = longnm;
  }

  private void condense() {
    if (packages.size() == 1 && classes.size() == 0) {
      PackageDescriptor child = packages.get(0);
      name = (name.equals(DEFAULT_PKG) ? "" : name + ".")  + child.name;
      longName = child.longName;
      packages = child.packages;
      classes = child.classes;
      // recursive call
      this.condense();
    }
    for (PackageDescriptor pkg : packages) {
      pkg.condense();
    }
  }

  private String topName(String qualifiedName) {
    String sqname = longName.length() > 0 ? qualifiedName.substring(longName.length() + 1)
                                          : qualifiedName;
    int idx = sqname.indexOf('.');
    if (idx < 0) {
      return  sqname;
    }
    return sqname.substring(0, idx);
  }

  protected void computeSubPackages(Collection<ClassDescriptor> values) {
    Map<String, ArrayList<ClassDescriptor>> subPkgs = Maps.newHashMap();
    for (ClassDescriptor cd : values) {
      if (cd.getPackageName().equals(longName)) {
        this.classes.add(cd);
      } else {
        String topPkg = topName(cd.getPackageName());
        ArrayList<ClassDescriptor> cs = subPkgs.get(topPkg);
        if (cs == null) {
          cs = Lists.newArrayList();
          subPkgs.put(topPkg, cs);
        }
        cs.add(cd);
      }
    }
    for (Entry<String, ArrayList<ClassDescriptor>> entry : subPkgs.entrySet()) {
      packages.add(newFrom(entry.getValue(), entry.getKey(),
          longName.length() > 0 ? longName + "." + entry.getKey()
              : entry.getKey()));
    }
  }

  public Map<String, ClassDescriptor> flatClasses() {
    Map<String, ClassDescriptor> map = Maps.newTreeMap();
    for (ClassDescriptor cls : this.getClasses()) {
      map.put(cls.getFullName(), cls);
    }
    for (PackageDescriptor subPkg : this.getPackages()) {
      map.putAll(subPkg.flatClasses());
    }
    return map;
  }

  public void addClass(ClassDescriptor cls) {
    this.classes.add(cls);
  }

  public void addPackage(PackageDescriptor pkg) {
    this.packages.add(pkg);
  }

  public Collection<ClassDescriptor> getClasses() {
    return classes;
  }

  public String getName() {
    return name;
  }

  public Collection<PackageDescriptor> getPackages() {
    return packages;
  }
}
