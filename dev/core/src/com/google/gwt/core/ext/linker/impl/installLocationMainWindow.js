// The GWT code can be installed anywhere, although an iFrame is the best
// approach if you want both variable isolation (useful in general, but
// critical if you want more than one GWT module on your page) and runAsync
// which will need to be able to install additional chunks of code into that
// isolated environment later on.

// The items that must be provided in any install location are:
// $wnd - the location where the bootstrap module is defined. Should also
//        be the location where the __gwtStatsEvent is defined

var wndInstalled = false;

function getInstallLocationDoc() {
  setupInstallLocation();
  return window.document;
}

// This function is left for compatibility
// and may be used by custom linkers
function getInstallLocation() {
  return getInstallLocationDoc().body;
}

function setupInstallLocation() {
  if (wndInstalled) { return; }
  var script = window.document.createElement('script');
  script.language='javascript';
  script.text = "var $wnd = window;";
  window.document.body.appendChild(script);
  wndInstalled = true;
}
