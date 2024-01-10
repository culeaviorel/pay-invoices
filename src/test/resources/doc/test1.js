let STORE = {
  EXTRA:0,
  ON:false,
};

let RemoteShelly = {
  _cb: function (result, error_code, error_message, callback) {
    let rpcResult = JSON.parse(result.body);
    let rpcCode = result.code;
    let rpcMessage = result.message;
    callback(rpcResult, rpcCode, rpcMessage);
  },
  composeEndpoint: function (method) {
    return "http://" + this.address + "/rpc/" + method;
  },
  call: function (rpc, data, callback) {
    let postData = {
      url: this.composeEndpoint(rpc),
      body: data,
    };
    Shelly.call("HTTP.POST", postData, RemoteShelly._cb, callback);
  },
  getInstance: function (address) {
    let rs = Object.create(this);
    // remove static method
    rs.getInstance = null;
    rs.address = address;
    return rs;
  },
};

let blueShelly1 = RemoteShelly.getInstance("192.168.68.133");
let blueShelly2 = RemoteShelly.getInstance("192.168.205.42");
let blueShelly3 = RemoteShelly.getInstance("192.168.205.101");

function setSwitch(swObj, how) {
  swObj.call(
    "switch.set",
    { id: 0, on: how },
    function (result, error_code, message) {
      print(JSON.stringify(result), error_code, message);
      STORE.ON = how;
    }
  );
}

function processHttpResponse(result, error_code, error) {
  if (error_code != 0) {
    //print(error)
  } else {
    var emeters = JSON.parse(result.body).emeters;
    var ret = emeters[0].power;
    //var pv = emeters[1].power;
    STORE.EXTRA = ret;
  }
}

function timerCode() {
  Shelly.call("HTTP.GET", {url: "http://192.168.68.105/status"}, processHttpResponse);

  if(STORE.EXTRA < -350){
    //Shelly.call("Switch.set", {'id': 0, 'on': true});
    if(STORE.ON){
      print("mic" + STORE.EXTRA)
      setSwitch(blueShelly1, false);
    }
  } else if (STORE.EXTRA < -450) {
    if(STORE.ON){
      print("mic" + STORE.EXTRA)
      setSwitch(blueShelly2, false);
    }
  } else {
    //Shelly.call("Switch.set", {'id': 0, 'on': false});
    if(!STORE.ON){
      print("mare" + STORE.EXTRA)
      setSwitch(blueShelly1, true);
    }
  }
};

Timer.set(2000, true, timerCode);