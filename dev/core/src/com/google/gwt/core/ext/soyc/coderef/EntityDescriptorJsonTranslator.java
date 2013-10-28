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

import com.google.gwt.core.ext.soyc.coderef.EntityDescriptor.Fragment;
import com.google.gwt.thirdparty.guava.common.collect.Maps;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Serialize/Deserialize EntityDescriptor instances to/from json.
 *
 */
public class EntityDescriptorJsonTranslator {

  public static final String ARTIFACT_NAME = "entities";

  private static JSONObject writeJsonFromEntity(EntityDescriptor entity) throws JSONException {
    JSONObject json = new JSONObject();
    JSONArray frags = new JSONArray();
    for (EntityDescriptor.Fragment frg : entity.getFragments()) {
      JSONObject frag = new JSONObject();
      frag.put("id", frg.getId());
      frag.put("size", frg.getSize());

      frags.put(frag);
    }
    json.put("fragments", frags);
    json.put("jsName", entity.getObfuscatedName());
    return json;
  }

  private static JSONObject writeJsonFromMember(MemberDescriptor entity) throws JSONException {
    JSONObject json = writeJsonFromEntity(entity);
    json.put("name", entity.getJsniSignature());
    return json;
  }

  public static JSONObject writeJson(PackageDescriptor pkg) throws JSONException {
    JSONObject json = new JSONObject();
    json.put("name", pkg.getName());
    // classes
    JSONArray clss = new JSONArray();
    for (ClassDescriptor cd : pkg.getClasses()) {
      JSONObject cls = writeJsonFromEntity(cd);
      cls.put("name", cd.getName());
      // fields
      JSONArray fs = new JSONArray();
      for (FieldDescriptor fd : cd.getFields()) {
        JSONObject fld = writeJsonFromMember(fd);
        fs.put(fld);
      }
      cls.put("fields", fs);
      // methods
      JSONArray ms = new JSONArray();
      for (MethodDescriptor md : cd.getMethods()) {
        JSONObject mth = writeJsonFromMember(md);
        mth.put("id", md.getUniqueId());
        mth.put("dependants", md.getDependantPointers());
        if (md.getMoreObfuscatedNames().size() > 0) {
          mth.put("otherJsNames", md.getMoreObfuscatedNames());
        }
        ms.put(mth);
      }
      cls.put("methods", ms);

      clss.put(cls);
    }
    json.put("classes", clss);
    // packages
    JSONArray pkgs = new JSONArray();
    for (PackageDescriptor pd : pkg.getPackages()) {
      pkgs.put(writeJson(pd));
    }
    json.put("packages", pkgs);

    return json;
  }

  public static PackageDescriptor readJson(JSONObject jsonObject) throws JSONException {
    return new Deserializer().readJson(jsonObject);
  }

  private static class Deserializer {

    private Map<Integer, MethodDescriptor> mapMethods = Maps.newHashMap();
    private Map<MethodDescriptor, JSONArray> mapDependants = Maps.newIdentityHashMap();

    private PackageDescriptor readJson(JSONObject jsonObject) throws JSONException{
      String pkgName = jsonObject.getString("name");
      PackageDescriptor packageDescriptor = readJsonPackage(jsonObject, pkgName,
          pkgName.equals(PackageDescriptor.DEFAULT_PKG) ? "" : pkgName);
      setMethodDependencies();
      return packageDescriptor;
    }

    private void setMethodDependencies() throws JSONException {
      for (MethodDescriptor method : mapDependants.keySet()) {
        JSONArray dependants = mapDependants.get(method);
        for (int i = 0; i < dependants.length(); i++) {
          method.addDependant(mapMethods.get(dependants.getInt(i)));
        }
      }
    }

    private PackageDescriptor readJsonPackage(JSONObject jsonObject, String name,
        String longName) throws JSONException {
      PackageDescriptor pkg = new PackageDescriptor(name, longName);
      JSONArray clss = jsonObject.getJSONArray("classes");
      for (int i = 0; i < clss.length(); i++) {
        pkg.addClass(readJsonClass(clss.getJSONObject(i), longName));
      }
      JSONArray pkgs = jsonObject.getJSONArray("packages");
      for (int i = 0; i < pkgs.length(); i++) {
        JSONObject subPkg = pkgs.getJSONObject(i);
        String pkgName = subPkg.getString("name");
        pkg.addPackage(readJsonPackage(subPkg, pkgName,
            longName + (longName.isEmpty() ? "" : ".") + pkgName));
      }
      return pkg;
    }

    private ClassDescriptor readJsonClass(JSONObject jsonObject, String pkgName)
        throws JSONException {
      ClassDescriptor cls = new ClassDescriptor(jsonObject.getString("name"), pkgName);
      updateEntity(cls, jsonObject);
      JSONArray flds = jsonObject.getJSONArray("fields");
      for (int i = 0; i < flds.length(); i++) {
        cls.addField(readJsonField(flds.getJSONObject(i), cls));
      }
      JSONArray mths = jsonObject.getJSONArray("methods");
      for (int i = 0; i < mths.length(); i++) {
        cls.addMethod(readJsonMethod(mths.getJSONObject(i), cls));
      }
      return cls;
    }

    private MethodDescriptor readJsonMethod(JSONObject jsonObject, ClassDescriptor cls)
        throws JSONException {
      MethodDescriptor method = new MethodDescriptor(cls, jsonObject.getString("name"));
      updateEntity(method, jsonObject);
      method.setUniqueId(jsonObject.getInt("id"));

      mapMethods.put(method.getUniqueId(), method);
      mapDependants.put(method, jsonObject.getJSONArray("dependants"));
      JSONArray jsNames = jsonObject.optJSONArray("otherJsNames");
      if (jsNames != null) {
        for (int i = 0; i < jsNames.length(); i++) {
          method.setObfuscatedName(jsNames.getString(i));
        }
      }

      return method;
    }

    private FieldDescriptor readJsonField(JSONObject jsonObject, ClassDescriptor cls)
        throws JSONException {
      String[] fullName = jsonObject.getString("name").split(":");
      FieldDescriptor fieldDescriptor = new FieldDescriptor(cls, fullName[0], fullName[1]);
      updateEntity(fieldDescriptor, jsonObject);
      return fieldDescriptor;
    }

    private void updateEntity(EntityDescriptor entity, JSONObject jsonObject) throws JSONException {
      entity.setObfuscatedName(jsonObject.optString("jsName"));
      JSONArray frags = jsonObject.getJSONArray("fragments");
      for (int i = 0; i < frags.length(); i++) {
        JSONObject frag = frags.getJSONObject(i);
        entity.addFragment(new Fragment(frag.getInt("id"), frag.getInt("size")));
      }
    }
  }

  private EntityDescriptorJsonTranslator() { }
}
