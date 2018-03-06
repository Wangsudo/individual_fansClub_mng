browserRedirect();
function browserRedirect() {
  var sUserAgent = navigator.userAgent.toLowerCase();
  var bIsIphoneOs = sUserAgent.match(/iphone os/i) == "iphone os";
  var bIsMidp = sUserAgent.match(/midp/i) == "midp";
  var bIsUc7 = sUserAgent.match(/rv:1.2.3.4/i) == "rv:1.2.3.4";
  var bIsUc = sUserAgent.match(/ucweb/i) == "ucweb";
  var bIsAndroid = sUserAgent.match(/android/i) == "android";
  var bIsCE = sUserAgent.match(/windows ce/i) == "windows ce";
  var bIsWM = sUserAgent.match(/windows mobile/i) == "windows mobile";
  if (bIsIphoneOs || bIsMidp || bIsUc7 || bIsUc || bIsAndroid || bIsCE || bIsWM) {
	  goPc(false);
  }else{
	  goPc(true);
  }
}
function goPc(flag){
	var pcLoc = mobileLoc= href = window.location.href;
	var index = href.indexOf("mobile");
	if(index>0 && flag){
		pcLoc = href.replace("mobile/","");
		location.href=pcLoc;
	}else if(index< 0 && !flag){
		index =  href.lastIndexOf("/");
		mobileLoc = href.substr(0,index)+"/mobile"+href.substr(index);
		location.href=mobileLoc;
	}
}
