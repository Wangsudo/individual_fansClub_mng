
var myInfo = {
	getInfo : function(){
		var _deId = "detron" + $("span[name=deId]").html();
		$.getJSON("http://211.166.9.218:8080/DetronVOIP/account/findByDeId?deId="+_deId+"&callback=?",
			function(mhJson){
	    		if (mhJson && mhJson.password)
	    			$("span[name=mhpwd]").html(mhJson.password);
	    		else
	    			$("span[name=mhpwd]").html('0');
	    		if (mhJson && mhJson.domain)
	    			$("span[name=domain]").html(mhJson.domain);
	    		else
	    			$("span[name=domain]").html('0');
	    	}
		);
		
		_deId = $("span[name=deId]").html();
		$.getJSON("http://211.166.9.202:8080/DFStorageServer2/userinfoAction/getUserInfo?deId="+_deId+"&callback=?",
			function(mpJson){
				if (mpJson && mpJson.remainMB)
					$("span[name=remainSpace]").html(mpJson.remainMB);
				else
					$("span[name=remainSpace]").html('0');
				if (mpJson && mpJson.totalMB)
					$("span[name=totalSpace]").html(mpJson.totalMB);
				else
					$("span[name=totalSpace]").html('0');
	    	}
		);
		
	}	
};

$(function(){
	myInfo.getInfo();
});