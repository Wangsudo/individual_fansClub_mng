
function getBrowserInfo()
{
	var agent = navigator.userAgent.toLowerCase() ;
	
	var regStr_ie = /msie [\d.]+;/gi ;
	var regStr_ff = /firefox\/[\d.]+/gi
	var regStr_chrome = /chrome\/[\d.]+/gi ;
	var regStr_saf = /safari\/[\d.]+/gi ;
	//IE
	if(agent.indexOf("msie") > 0)
	{
	return agent.match(regStr_ie) ;
	}
	
	//firefox
	if(agent.indexOf("firefox") > 0)
	{
	return agent.match(regStr_ff) ;
	}
	
	//Chrome
	if(agent.indexOf("chrome") > 0)
	{
	return agent.match(regStr_chrome) ;
	}
	
	//Safari
	if(agent.indexOf("safari") > 0 && agent.indexOf("chrome") < 0)
	{
	return agent.match(regStr_saf) ;
	}
}

function has(str){
	if(str=='ie'){
		var browser = getBrowserInfo() ;
		//alert(browser); 
		return (browser+"").replace(/[^0-9.]/ig,""); 
	}else if(str =='safari' ){
		getBrowserInfo
	}
};

function _isIE11() {
        var iev = 0;
        var ieold = (/MSIE (\d+\.\d+);/.test(navigator.userAgent));
        var trident = !!navigator.userAgent.match(/Trident\/7.0/);
        var rv = navigator.userAgent.indexOf("rv:11.0");

        if (ieold) {
          iev = Number(RegExp.$1);
        }
        if (navigator.appVersion.indexOf("MSIE 10") !== -1) {
          iev = 10;
        }
        if (trident && rv !== -1) {
          iev = 11;
        }

        return iev === 11;
      }

      function _isEdge() {
        return /Edge\/12/.test(navigator.userAgent);
      }

      function _getDownloadUrl(text) {
        var BOM = "\uFEFF";
        // Add BOM to text for open in excel correctly
        if (window.Blob && window.URL && window.URL.createObjectURL) {
          var csvData = new Blob([BOM + text], { type: 'text/csv' });
          return URL.createObjectURL(csvData);
        } else {
          return 'data:attachment/csv;charset=utf-8,' + BOM + encodeURIComponent(text);
        }
      };
      
      function download(filename, text) {
          if (has('ie') && has('ie') < 10) {
            // has module unable identify ie11 and Edge
            var oWin = window.top.open("about:blank", "_blank");
            oWin.document.write(text);
            oWin.document.close();
            oWin.document.execCommand('SaveAs', true, filename);
            oWin.close();
          }else if (has("ie") === 10 || _isIE11() || _isEdge()) {
            var BOM = "\uFEFF";
            var csvData = new Blob([BOM + text], { type: 'text/csv' });
            navigator.msSaveBlob(csvData, filename);
          } else {
            var link=document.createElement("a");
            link.setAttribute('href',_getDownloadUrl(text));
            link.setAttribute('download',filename);
            document.body.appendChild(link);
            if ((getBrowserInfo()+"").indexOf('safari')>-1) {
              // # First create an event
              var click_ev = document.createEvent("MouseEvents");
              // # initialize the event
              click_ev.initEvent("click", true /* bubble */ , true /* cancelable */ );
              // # trigger the evevnt/
              link.dispatchEvent(click_ev);
            } else {
              link.click();
            }

            document.body.removeChild(link);
          }
        }
      
