// GWT code can be installed anywhere, but an iFrame is the best place if you
// want both variable isolation and runAsync support. Variable isolation is
// useful for avoiding conflicts with JavaScript libraries and critical if
// you want more than one GWT module on your page. The runAsync implementation
// will need to install additional chunks of code into the same iFrame later.
//
// By default, CrossSiteIFrameLinker will use this script to create the iFrame.
// It may be replaced by overriding CrossSiteIframeLinker.getJsInstallLocation()
// to return the name of a different resource file. The replacement script may
// optionally set this variable inside the iframe:
//
// $wnd - the location where the bootstrap module is defined. It should also
//        be the location where the __gwtStatsEvent function is defined.
//        If not set, the module will set $wnd to window.parent.

var frameDoc;

function getInstallLocationDoc() {
  setupInstallLocation();
  return frameDoc;
}

// This function is left for compatibility
// and may be used by custom linkers
function getInstallLocation() {
  return getInstallLocationDoc().body;
}

function setupInstallLocation() {
  if (frameDoc) { return; }

  // Remove any stale bootstrap iframe left over from a previous page load
  // (e.g. after a Shift+Reload in Safari) to prevent accumulation and avoid
  // inheriting a throttled browsing-context state.
  var stale = $doc.getElementById('__MODULE_NAME__');
  if (stale && stale.parentNode) {
    stale.parentNode.removeChild(stale);
  }

  // Create the script frame, making sure it's invisible, but not
  // "display:none", which keeps some browsers from running code in it.
  //
  // The iframe must have a non-zero intersection with the viewport.
  // Safari (WebKit) throttles DOM timers in iframes whose visible rect is
  // empty, which includes iframes placed off-screen at negative coordinates
  // as well as those hidden via visibility:hidden or opacity:0.  A 50x50px
  // fixed element at the top-left corner with opacity:0.01 is imperceptible
  // to users while reliably preventing the throttle.
  var scriptFrame = $doc.createElement('iframe');
  scriptFrame.id = '__MODULE_NAME__';
  scriptFrame.style.cssText = 'position:fixed; left:0; top:0; width:50px; height:50px;'
    + ' border:none; opacity:0.01;';
  scriptFrame.tabIndex = -1;
  $doc.body.appendChild(scriptFrame);

  frameDoc = scriptFrame.contentWindow.document;

  // The following code is needed for proper operation in Firefox and
  // Internet Explorer.
  //
  // In Firefox, this prevents the frame from re-loading asynchronously and
  // throwing away the current document.
  //
  // In IE, it ensures that the <body> element is immediately available.
  //
  // Safari is excluded because document.write() + document.close() triggers
  // a navigation cycle that puts Safari's browsing context into a throttled
  // state, causing the same multi-minute delay as the off-screen iframe style.
  var isSafari = navigator.vendor && navigator.vendor.indexOf("Apple") != -1;
  if (navigator.userAgent.indexOf("Chrome") == -1 && !isSafari) {
    frameDoc.open();
    var doctype = (document.compatMode == 'CSS1Compat') ? '<!doctype html>' : '';
    frameDoc.write(doctype + '<html><head></head><body></body></html>');
    frameDoc.close();
  }
}
