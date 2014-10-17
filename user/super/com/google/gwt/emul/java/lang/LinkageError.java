/*
 * Copyright 2007 Google Inc.
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
package java.lang;

/**
 * See <a href="http://docs.oracle.com/javase/7/docs/api/java/lang/LinkageError.html">the
 * official Java API doc</a> for details.
 * 
 * This exception is never thrown by GWT or GWT's libraries, as GWT does not support reflection. It
 * is provided in GWT only for compatibility with test code
 * which makes assertions about user code .class dependencies.
 */
public class LinkageError extends Error {
  public LinkageError() {
  }

  public LinkageError(String s) {
    super(s);
  }

  public LinkageError(String s, Throwable cause) {
    super(s, cause);
  }
}
