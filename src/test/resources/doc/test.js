let STORE = {
  EXTRA:0
};

function processHttpResponse(result, error_code, error) {
  if (error_code != 0) {
    //print(error)
  } else {
    var emeters = JSON.parse(result.body).emeters;
    var l1 = emeters[0].power;
    var l2 = emeters[1].power;
    STORE.EXTRA = l1-l2;
  }
}

function timerCode() {
  Shelly.call("HTTP.GET", {url: "http://192.168.68.105/status"}, processHttpResponse);
  print(STORE.EXTRA)
  if(STORE.EXTRA < 283){
    Shelly.call("Switch.set", {'id': 0, 'on': true});
  } else {
    Shelly.call("Switch.set", {'id': 0, 'on': false});
  }
};

Timer.set(1000, true, timerCode);