/*
 * Copyright 2011 Google Inc.
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
package com.google.gwt.media.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.dom.client.MediaElement;
import com.google.gwt.dom.client.SourceElement;
import com.google.gwt.event.dom.client.LoadedMetadataEvent;
import com.google.gwt.event.dom.client.LoadedMetadataHandler;
import com.google.gwt.junit.DoNotRunWith;
import com.google.gwt.junit.Platform;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.media.dom.client.MediaError;

import junit.framework.Assert;

/**
 * Base test for {@link MediaBase}.
 *
 *  Do not call this class directly. To use, extend this class and override the
 * getElement and isSupported methods.
 *
 *  Because HtmlUnit does not support HTML5, you will need to run these tests
 * manually in order to have them run. To do that, go to "run configurations" or
 * "debug configurations", select the test you would like to run, and put this
 * line in the VM args under the arguments tab: -Dgwt.args="-runStyle Manual:1"
 */
@DoNotRunWith(Platform.HtmlUnitUnknown)
public abstract class MediaTest extends GWTTestCase {

  static native boolean isFirefox35OrLater() /*-{
    var geckoVersion = @com.google.gwt.dom.client.DOMImplMozilla::getGeckoVersion()();
    return (geckoVersion != -1) && (geckoVersion >= 1009001);
  }-*/;

  static native boolean isFirefox40OrEarlier() /*-{
    return @com.google.gwt.dom.client.DOMImplMozilla::isGecko2OrBefore()();
  }-*/;

  static native boolean isIE8() /*-{
    return $wnd.navigator.userAgent.toLowerCase().indexOf('msie') != -1 && $doc.documentMode == 8;
  }-*/;

  static native boolean isOldFirefox() /*-{
    return @com.google.gwt.dom.client.DOMImplMozilla::isGecko191OrBefore()();
  }-*/;

  public void disabled_testPreload() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    if (isFirefox40OrEarlier()) {
      return; // don't continue on older versions of Firefox.
    }

    String state = media.getPreload();
    assertNotNull(state);
    assertTrue("Illegal preload state", state.equals(MediaElement.PRELOAD_AUTO)
        || state.equals(MediaElement.PRELOAD_METADATA)
        || state.equals(MediaElement.PRELOAD_NONE));

    media.setPreload(MediaElement.PRELOAD_METADATA);
    assertEquals("Preload should be able to be set.",
        MediaElement.PRELOAD_METADATA, media.getPreload());
  }

  /**
   * Return the Media associated with the test.
   *
   * @return the Media associated with the test.
   */
  public abstract MediaBase getMedia();

  @Override
  public String getModuleName() {
    return "com.google.gwt.media.MediaTest";
  }

  public void testAddSource() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    // Add some source elements.
    SourceElement source0 = media.addSource("file.ogg", "audio/ogg");
    assertTrue(source0.getSrc().endsWith("file.ogg"));
    assertEquals("audio/ogg", source0.getType());
    SourceElement source1 = media.addSource("file.ogv", "video/ogg");
    assertTrue(source1.getSrc().endsWith("file.ogv"));
    assertEquals("video/ogg", source1.getType());

    // Add a source without a type.
    SourceElement source2 = media.addSource("file.mp3");
    assertTrue(source2.getSrc().endsWith("file.mp3"));

    // Check that the sources are a children of the media.
    assertEquals(media.getElement(), source0.getParentElement());
    assertEquals(media.getElement(), source1.getParentElement());
    assertEquals(media.getElement(), source2.getParentElement());
  }

  public void testAutoPlay() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    media.setAutoplay(false);
    assertFalse("Autoplay should be off.", media.isAutoplay());
    media.setAutoplay(true);
    assertTrue("Autoplay should be on.", media.isAutoplay());
  }

  public void testControls() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    media.setControls(false);
    assertFalse("Controls should be off.", media.hasControls());
    media.setControls(true);
    assertTrue("Controls should be on.", media.hasControls());
  }

  public void testCurrentSrc() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    media.load();
    Assert.assertNotNull(
        "currentSrc should be set in these tests.", media.getCurrentSrc());
  }

  public void testPlayAndSeek() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    assertAfterLoad(new RepeatingCommand() {
      boolean afterSeek = false;
      @Override
      public boolean execute() {
        if (media.getCurrentTime() >= 1) {
          assertFalse(afterSeek);
          media.setCurrentTime(0); // seek to a previous time
          afterSeek = true;
          // In the next loop we will assert the time to check if the seek is successful
          return true;
        }

        if (afterSeek) {
          assertTrue(media.getCurrentTime() < 1);
          return false;
        }

        // Need more time to complete 1 second of play
        return true;
      }
    });
    media.play();
  }

  public void testLoad() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    assertAfterLoad(new RepeatingCommand() {
      @Override
      public boolean execute() {
        assertNoErrors(media);
        return false;
      }
    });
    media.load();
  }

  public void testLoop() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    media.setLoop(false);
    assertFalse("Loop should be off.", media.isLoop());
    media.setLoop(true);
    assertTrue("Loop should be on.", media.isLoop());
  }

  public void testMuted() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    media.setMuted(true);
    assertTrue("Muted should be true.", media.isMuted());
    media.setMuted(false);
    assertFalse("Muted should be false.", media.isMuted());
  }

  public void testNetworkState() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    int state = media.getNetworkState();
    assertTrue("Illegal network state", state == MediaElement.NETWORK_EMPTY
        || state == MediaElement.NETWORK_IDLE
        || state == MediaElement.NETWORK_LOADING
        || state == MediaElement.NETWORK_NO_SOURCE);
  }

  public void testPlaybackRate() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    assertEquals("Default playback rate should be 1.0", 1.0,
        media.getDefaultPlaybackRate());

    assertAfterLoad(new RepeatingCommand() {
      @Override
      public boolean execute() {
        assertNoErrors(media);

        // set rate to 2.0
        double rate = 2.0;
        media.setPlaybackRate(rate);
        assertEquals("Should be able to change playback rate", rate,
            media.getPlaybackRate());

        // return to 1.0
        rate = 1.0;
        media.setPlaybackRate(rate);
        assertEquals("Should be able to change playback rate", rate,
            media.getPlaybackRate());

        return false;
      }
    });
    media.play();
  }

  public void testReadyState() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    int state = media.getReadyState();
    assertTrue("Illegal ready state", state == MediaElement.HAVE_CURRENT_DATA
        || state == MediaElement.HAVE_ENOUGH_DATA
        || state == MediaElement.HAVE_FUTURE_DATA
        || state == MediaElement.HAVE_METADATA
        || state == MediaElement.HAVE_NOTHING);
  }

  public void testRemoveSource() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    // Add some source elements.
    SourceElement source0 = media.addSource("file.ogg", "audio/ogg");
    SourceElement source1 = media.addSource("file.ogv", "video/ogg");
    SourceElement source2 = media.addSource("file.mp3");
    assertEquals(media.getElement(), source0.getParentElement());
    assertEquals(media.getElement(), source1.getParentElement());
    assertEquals(media.getElement(), source2.getParentElement());

    // Remove a source.
    media.removeSource(source1);
    assertEquals(media.getElement(), source0.getParentElement());
    assertNull(source1.getParentElement());
    assertEquals(media.getElement(), source2.getParentElement());

    // Let a source remove itself.
    source2.removeFromParent();
    assertEquals(media.getElement(), source0.getParentElement());
    assertNull(source1.getParentElement());
    assertNull(source2.getParentElement());

    // Remove a source that is not a child.
    media.removeSource(source0);
  }

  public void testSupported() {
    // test the isxxxSupported() call if running known sup or not sup browsers.
    if (isIE8()) {
      assertFalse(Audio.isSupported());
      assertFalse(Video.isSupported());
    }
    if (isFirefox35OrLater()) {
      assertTrue(Audio.isSupported());
      assertTrue(Video.isSupported());
    }
  }

  public void testVolume() {
    final MediaBase media = getMedia();
    if (media == null) {
      return; // don't continue if not supported
    }

    media.setVolume(0.5);
    assertEquals(
        "Volume should be at one-half loudness.", 0.5, media.getVolume());
    media.setVolume(0.75);
    assertEquals("Volume should be at three-quarters loudness.", 0.75,
        media.getVolume());
  }

  private void assertAfterLoad(final RepeatingCommand command) {
    // the media resource needs time to load
    delayTestFinish(10 * 1000);

    getMedia().addLoadedMetadataHandler(new LoadedMetadataHandler() {

      @Override
      public void onLoadedMetadata(LoadedMetadataEvent event) {
        assertNoErrors(getMedia());

        Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

          @Override
          public boolean execute() {
            assertNoErrors(getMedia());

            boolean finished = !command.execute();
            if (finished) {
              finishTest();
              return false;
            }
            return true;
          }
        }, 100);
      }
    });
  }

  private static void assertNoErrors(final MediaBase media) {
    MediaError error = media.getError();
    if (error != null) {
      fail("Media error (" + error.getCode() + ")");
    }
  }
}
