const http = require('http');
const fs = require('fs');

var data={}
var category={}
var refer={}
var redeem={}
var metadata={}
var mods={}
var comments={}
var timesCalled=0
fs.readFile('data.json', "utf-8", (err, datatest) => {
    if (err) throw err;
    data=JSON.parse(datatest)
    console.log('Parsed Data')
  }
);
fs.readFile('category.json', "utf-8", (err, datatest) => {
    if (err) throw err;
    category=JSON.parse(datatest)
    console.log('Parsed Category')
  }
);
fs.readFile('refer.json', "utf-8", (err, datatest) => {
    if (err) throw err;
    refer=JSON.parse(datatest)
    console.log('Parsed Referral')
  }
);
fs.readFile('redeem.json', "utf-8", (err, datatest) => {
    if (err) throw err;
    redeem=JSON.parse(datatest)
    console.log('Parsed Redeem')
  }
);
fs.readFile('metadata.json', "utf-8", (err, datatest) => {
    if (err) throw err;
    metadata=JSON.parse(datatest)
    console.log('Parsed Metadata')
  }
);
fs.readFile('comments.json', "utf-8", (err, datatest) => {
    if (err) throw err;
    comments=JSON.parse(datatest)
    console.log('Parsed Comments')
  }
);
fs.readFile('mods.json', "utf-8", (err, datatest) => {
    if (err) throw err;
    mods=JSON.parse(datatest)
    console.log('Parsed Mods')
  }
);


http.createServer(options,function (req, res){    
    try{
      res.writeHead(200, {'Content-Type': 'text/plain/html; charset=utf-8'});
      
      //Setting stuff
      var out=""
      requrl=req.url.substring(1)
      var logStream   
      console.log(Date().toString()+";"+req.url)
      
      
      
      key=requrl.substring(0,requrl.indexOf("/"))


      //Saving data every 20 requests
      timesCalled++    
      if(timesCalled>20){
        timesCalled=0
        fs.writeFile('data.json', JSON.stringify(data), (err) => {
	    if (err) throw err;
	});
	fs.writeFile('category.json', JSON.stringify(category), (err) => {
	    if (err) throw err;
	});
	fs.writeFile('refer.json', JSON.stringify(refer), (err) => {
	    if (err) throw err;
	});
	fs.writeFile('redeem.json', JSON.stringify(redeem), (err) => {
	    if (err) throw err;
	});
	fs.writeFile('metadata.json', JSON.stringify(metadata), (err) => {
	    if (err) throw err;
	});
	fs.writeFile('comments.json', JSON.stringify(comments), (err) => {
	    if (err) throw err;
	});
	fs.writeFile('mods.json', JSON.stringify(mods), (err) => {
	    if (err) throw err;
	});
        
        console.log(`;Changed=1\n`)       
                
      }
      else{
        console.log(`;Changed=0\n`)         
        
      }
      
      
      //Keys
      validKeys=[<your server keys here>]
      if(!validKeys.includes(key)){
        res.write("invalid key")
        return res.end()
      }

      //Parsing mode and path from req
      path=requrl.substring(requrl.indexOf("/")+1) 
      
      
      
      mode=path
      if(path.indexOf("/")!=-1){
        mode=path.substring(0,path.indexOf("/"))
        path=path.substring(path.indexOf("/")+1)   
      }
      
      //else-if ladder for different modes
      if(mode=="r"){
        try{
          senddatatest = getdata(path)
        } 
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        res.write(`${JSON.stringify(senddatatest)}`)        
        return res.end();
        
      }
      else if(mode=="updateElements"){ 
        userID=path.substring(0,path.indexOf("/"))
        wdata=path.substring(path.indexOf("/")+1)   
        try{
          data[`users`][userID][`elements`]=`${wdata}`          
          res.write(`1`)         
        }
        catch{
          console.log(`ERROR: ${err}`)
        }        
        return res.end();
      }
      else if(mode=="updateBal"){    
        userID=path.substring(0,path.indexOf("/"))
        wdata=path.substring(path.indexOf("/")+1)
        try{
          data[`users`][userID].bal=`${wdata}`                     
          res.write(`1`)         
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();        
      }
      else if(mode=="addLog0"){
        userID=path.substring(0,path.indexOf("/"))
        add=path.substring(path.indexOf("/")+1,path.indexOf("/",path.indexOf("/")+1))
        path=path.substring(path.indexOf("/")+1)
        msg=path.substring(path.indexOf("/")+1)
        msg=msg.split('+').join(' ')
        msg=decodeURIComponent(msg)
        try{
          data[`users`][userID][`log`][Date.now()]={add:`${add}`,msg:`${msg}`,type:`0`}
          res.write(`1`)          
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();        
      }
      
      else if(mode=="addLog1"){
        userID=path.substring(0,path.indexOf("/"))
        add=path.substring(path.indexOf("/")+1,path.indexOf("/",path.indexOf("/")+1))
        path=path.substring(path.indexOf("/")+1)
        unlocked=path.substring(path.indexOf("/")+1)
        unlocked=unlocked.split('+').join(' ')
        unlocked=decodeURIComponent(unlocked)
        try{
          data[`users`][userID][`log`][Date.now()]={add:`${add}`,unlocked:`${unlocked}`,type:`1`}
          res.write(`1`)           
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();     
      }
      else if(mode=="addLog2"){
        userID=path.substring(0,path.indexOf("/"))
        add=path.substring(path.indexOf("/")+1,path.indexOf("/",path.indexOf("/")+1))
        path=path.substring(path.indexOf("/")+1)
        name=path.substring(path.indexOf("/")+1)
        name=name.split('+').join(' ')
        name=decodeURIComponent(name)
        try{
          data[`users`][userID][`log`][Date.now()]={add:`${add}`,name:`${name}`,type:`2`}
          res.write(`1`)           
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();          
      }
      else if(mode=="addLog3"){
        userID=path.substring(0,path.indexOf("/"))
        add=path.substring(path.indexOf("/")+1,path.indexOf("/",path.indexOf("/")+1))
        path=path.substring(path.indexOf("/")+1)
        name=path.substring(path.indexOf("/")+1)
        name=name.split('+').join(' ')
        name=decodeURIComponent(name)
        try{
          data[`users`][userID][`log`][Date.now()]={add:`${add}`,name:`${name}`,type:`3`}
          res.write(`1`)           
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();          
      }
      else if(mode=="addLog4"){
        userID=path.substring(0,path.indexOf("/"))
        add=path.substring(path.indexOf("/")+1,path.indexOf("/",path.indexOf("/")+1))
        path=path.substring(path.indexOf("/")+1)
        from=path.substring(path.indexOf("/")+1)
        from=from.split('+').join(' ')
        from=decodeURIComponent(from)
        try{
          data[`users`][userID][`log`][Date.now()]={add:`${add}`,from:`${from}`,type:`4`}
          res.write(`1`)           
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="getActiveVotes"||path=="getActiveVotes"){
        try{
          activeVotes= {}    
          var userCount=0
          for(each in data.users)
          	userCount++
          console.log(userCount)           
          for(indVote in data.votes){
            if(data[`votes`][indVote][`added`]=="0"){                
              if(data[`votes`][indVote][`upvoted`]==null||data[`votes`][indVote][`upvoted`]=="undefined")
                data[`votes`][indVote][`upvoted`]=""
              if(data[`votes`][indVote][`downvoted`]==null||data[`votes`][indVote][`downvoted`]=="undefined")
                data[`votes`][indVote][`downvoted`]=""  
              downvotedUsers=data[`votes`][indVote][`downvoted`].split(",")
	      upvotedUsers=data[`votes`][indVote][`upvoted`].split(",")     
              data[`votes`][indVote].vote=`${upvotedUsers.length-downvotedUsers.length}`           
              activeVotes[indVote]=data[`votes`][indVote]   
              var userID=activeVotes[indVote][`user`]
              var time=Number(activeVotes[indVote][`time`])
              var needed = Math.trunc(Math.log2(userCount)) 
              needed -= Math.trunc((Date.now()-time)/(86400000*2)) 
              originalVote=Number(activeVotes[indVote][`vote`])
              
              originalVote-=Math.trunc((Date.now()-time)/(86400000*4)) 
              
              activeVotes[indVote][`vote`]=`${originalVote}`
              activeVotes[indVote][`username`]=data[`users`][userID][`name`]  
              activeVotes[indVote][`needed`]= needed     
            }
            else{
              delete data[`votes`][indVote]
            }
          }   
          res.write(JSON.stringify(activeVotes)) 
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }             
        return res.end();
      }
      else if(mode=="getUserCount"||path=="getUserCount"){
        try{
          userCount=0   
          for(indVote in data.users){
            userCount++
          }
          res.write(`${userCount}`)
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }         
        return res.end();
      }
      else if(mode=="upvote"){
        try{
          userID=path.substring(0,path.indexOf("/"))
          voteID=path.substring(path.indexOf("/")+1)
          response=0
          downvotedUsers=data[`votes`][voteID][`downvoted`].split(",")
          upvotedUsers=data[`votes`][voteID][`upvoted`].split(",")                        
          if(upvotedUsers.includes(userID)){
            upvotedUsers=removeA(upvotedUsers,userID)            
          }
          else{
            upvotedUsers.push(userID)
            response=1
          }  
          if(downvotedUsers.includes(userID)){
            downvotedUsers=removeA(downvotedUsers,userID)
          }      
          data[`votes`][voteID].downvoted=`${downvotedUsers}`
          data[`votes`][voteID].upvoted=`${upvotedUsers}`
          data[`votes`][voteID].vote=`${upvotedUsers.length-downvotedUsers.length}`
          userCount=0   
          for(indVote in data.users){
            userCount++
          }
          var time=Number(data[`votes`][voteID][`time`])
          needed=Math.trunc(Math.log2(userCount))     
          needed -= Math.trunc((Date.now()-time)/(86400000*2)) 
	  newVote=Number(data[`votes`][voteID][`vote`])
	  newVote-=Math.trunc((Date.now()-time)/(86400000*4)) 
	   
          if(newVote>=needed){
            response=2
            newID=-1
            currID=0
            for(element in data.elements){
              if(data[`votes`][voteID][`name`]==data[`elements`][element][`name`]&&newID==-1){
                newID=currID
              }
              currID++
            }
            var elementName = data[`votes`][voteID][`name`]
            sendNotification(data[`votes`][voteID][`user`],`Your suggestion of ${elementName} is added to the game`,`${elementName} got enough votes that it got added to the game! Come, be the first one to unlock it.`)
            var loopi=0
            while(loopi<upvotedUsers.length){
            	if(`${upvotedUsers[loopi]}`!=data[`votes`][voteID][`user`]&&`${upvotedUsers[loopi]}`!=userID)
            		sendNotification(`${upvotedUsers[loopi]}`,`The element you upvoted is added to the game!`,`The element you upvoted [${elementName}] got enough votes that it got added to the game! Come, be the first one to unlock it.`)
            	loopi++
            }
            
            if(newID!=-1){
              data[`addedVote`][Date.now()]=`From ${data[`votes`][voteID][`from`]},Makes ${newID},Name ${data[`votes`][voteID][`name`]},Color ${data[`votes`][voteID][`color`]},User ${data[`votes`][voteID][`user`]}`
              data[`combinations`].push({from:data[`votes`][voteID][`from`],makes:`${newID}`})
              data[`combination2`][`${data[`votes`][voteID][`from`]}`]=`${newID}`
            }
            else{
              data[`addedVote`][Date.now()]=`From ${data[`votes`][voteID][`from`]},Makes ${currID},Name ${data[`votes`][voteID][`name`]},Color ${data[`votes`][voteID][`color`]},User ${data[`votes`][voteID][`user`]}`
              data[`elements`].push({by:data[`votes`][voteID][`user`],color:data[`votes`][voteID][`color`],name:data[`votes`][voteID][`name`],"time":Date.now()})
              metadata[`elementNameToID`][data[`votes`][voteID][`name`]]=`${currID}`
              data[`combinations`].push({from:data[`votes`][voteID][`from`],makes:`${currID}`})
              data[`combination2`][`${data[`votes`][voteID][`from`]}`]=`${currID}`
            }
            tempUser=`${data[`votes`][voteID][`user`]}`
            data[`users`][tempUser][`log`][Date.now()]={add:`+15`,name:`${data[`votes`][voteID][`name`]}`,type:`3`}
            data[`users`][tempUser][`bal`]=`${Number(data[`users`][tempUser][`bal`])+15}`
            from=data[`votes`][voteID][`from`]
            for(each in data[`votes`]){
          	if(data[`votes`][each][`from`]==from){
          		delete data[`votes`][each]
          	}
            }
            delete data[`votes`][voteID]
          }
          else{
          	var elementName = data[`votes`][voteID][`name`]
          	var userName = data[`users`][userID][`name`]
          	if(metadata[`votes`]==null)
          		metadata[`votes`]={}
          	if(metadata[`votes`][voteID]==null)
          		metadata[`votes`][voteID]={}
          	if(metadata[`votes`][voteID][`upvotedOnce`]==null){
          		metadata[`votes`][voteID][`upvotedOnce`]=`${userID}`
          		sendNotification(data[`votes`][voteID][`user`],`${userName} upvoted your suggestion of ${elementName}`,"")
          	}
          	else{
	          	var temp=metadata[`votes`][voteID][`upvotedOnce`]
          		if(!temp.split(",").includes(userID)){          			
          			metadata[`votes`][voteID][`upvotedOnce`]=`${temp},${userID}`
          			sendNotification(data[`votes`][voteID][`user`],`${userName} upvoted your suggestion of ${elementName}`,"")
          		}     
          	}


          }        
          res.write(`${response}`)
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }        
        return res.end();        
      }
      else if(mode=="downvote"){
        try{
          userID=path.substring(0,path.indexOf("/"))
          voteID=path.substring(path.indexOf("/")+1)
          response=0            
          downvotedUsers=data[`votes`][voteID][`downvoted`].split(",")
          upvotedUsers=data[`votes`][voteID][`upvoted`].split(",")                 
          if(downvotedUsers.includes(userID)){
            downvotedUsers=removeA(downvotedUsers,userID)
          }
          else{
            downvotedUsers.push(userID)
            response=-1
          }  
          if(upvotedUsers.includes(userID)){
            upvotedUsers=removeA(upvotedUsers,userID)            
          }      
          data[`votes`][voteID].downvoted=`${downvotedUsers}`
          data[`votes`][voteID].upvoted=`${upvotedUsers}`
          data[`votes`][voteID].vote=`${upvotedUsers.length-downvotedUsers.length}`
          userCount=0   
          for(indVote in data.users){
            userCount++
          }
          var time=Number(data[`votes`][voteID][`time`])
          needed=Math.log2(userCount)
          needed -= Math.trunc((Date.now()-time)/(86400000*2)) 
	  newVote=Number(data[`votes`][voteID][`vote`])
	  newVote-=Math.trunc((Date.now()-time)/(86400000*4)) 
          needed=Math.trunc(needed/2)
          needed*=-1     
                       
          if(newVote<=needed){   
            response=-2       
            data[`removedVote`][Date.now()]=`From ${data[`votes`][voteID][`from`]},Name ${data[`votes`][voteID][`name`]},Color ${data[`votes`][voteID][`color`]},User ${data[`votes`][voteID][`user`]}`          
            delete data[`votes`][voteID]
          }          
          res.write(`${response}`)
          return res.end();
        }
        catch(err){
          console.log(`${err}`)
        }              
      }
      else if(mode=="getUsername"){
        try{
          userIds=path.split(",")
          reqUser={}
          for(id in userIds){          
            reqUser[`${userIds[id]}`]=data[`users`][`${userIds[id]}`][`name`]
          }
          res.write(JSON.stringify(reqUser))          
        }
        catch(err){
          console.log(`${err}`)
        }
        return res.end();                
      }
      else if(mode=="ping"||path=="ping"){
        res.write(`${Date.now()}`)
        res.end()
      }
      else if(mode=="addComment"){        
        try{
          userID=path.substring(0,path.indexOf("/"))
          voteID=path.substring(path.indexOf("/")+1,path.indexOf("/",path.indexOf("/")+1))
          path=path.substring(path.indexOf("/")+1)
          umsg=path.substring(path.indexOf("/")+1)
          umsg=umsg.split('+').join(' ')
          umsg=decodeURIComponent(umsg)
          var userVote=data[`votes`][voteID][`user`]
          var nameVote=data[`votes`][voteID][`name`]
          console.log(userVote)
          if(userVote!=userID){
	          data[`users`][userVote][`log`][Date.now()]={add:`+0`,msg:`New Comment;u/${userID}/ commented on your suggestion of new element of ${nameVote}`,type:`0`}
	          var username = data[`users`][userID][`name`]
	          sendNotification(userVote,`${username} commented on ${nameVote}`,umsg)
	  }
	  else
	  	  data[`users`][userVote][`log`][Date.now()]={add:`+0`,msg:`New Comment;You commented on your suggestion of new element of ${nameVote}`,type:`0`}
          if(data[`votes`][voteID][`added`]!=null){
            if(data[`votes`][voteID][`comments`]!=null){
              data[`votes`][voteID][`comments`][Date.now()]={uid:`${userID}`,msg:`${umsg}`}
            }
            else{
              data[`votes`][voteID][`comments`]={}
              data[`votes`][voteID][`comments`][Date.now()]={uid:`${userID}`,msg:`${umsg}`}
            }
          }
          
          res.write("1")
        }
        catch(err){
          console.log(`${err}`)
        }
        return res.end();
        
      }
      else if(mode=="removeVoteComment"){        
        try{                  
          by=path.substring(0,path.indexOf("/"))
	  path=path.substring(path.indexOf("/")+1)
          voteID=path.substring(0,path.indexOf("/"))
          commentID=path.substring(path.indexOf("/")+1)
          activeMods=mods[`0`].split(",")
          
          if(data[`votes`][voteID][`comments`]!=null){
	    userID=data[`votes`][voteID][`comments`][commentID][`uid`]
	    if(activeMods.includes(by)||by==userID){
	    	voteName=data[`votes`][voteID][`name`]
	    	if(by!=userID){
          		modName=data[`users`][by][`name`]
          		comment=data[`votes`][voteID][`comments`][commentID][`msg`]
          		bot.channels.cache.get('742724997889523793').send(modName+" removed comment ["+comment+"] on vote "+voteName)
          	}
	    
          	data[`votes`][voteID][`comments`][commentID]={uid:`${userID}`,msg:`[Deleted comment]`}
          	data[`users`][userID][`log`][Date.now()]={add:`-0`,msg:`Violation;Your comment on ${voteName} got removed. This game is 13+ and aimed at general public, so please be humane while writing comments.`,type:`0`}
		res.write("1")
	    }
	    else
	  	res.write("mod");
	    
	  }
	  else{
	        res.write("invalid")
	  }
          
          
          
          
        }
        catch(err){
          console.log(`${err}`)
        }
        return res.end();
        
      }
      else if(mode=="addVote"){
        try{
          name=path.substring(0,path.indexOf("/"))
          color=path.substring(path.indexOf("/")+1,path.indexOf("/",path.indexOf("/")+1))
          path=path.substring(path.indexOf("/")+1)
          from=path.substring(path.indexOf("/")+1,path.indexOf("/",path.indexOf("/")+1))
          path=path.substring(path.indexOf("/")+1)
          user=path.substring(path.indexOf("/")+1)
          name=name.split('+').join(' ')
          name=decodeURIComponent(name)
          addVote=true
          if(name.length==0)
          	addVote=false
          for(each in data[`votes`]){
          	if(data[`votes`][each][name]==name && data[`votes`][each][from]==from){
          		addVote=false
          		break
          	}
          }
          if(name.length>128)
          	addVote=false
          if(addVote){
		  if(data[`votes`]==null){
		    data[`votes`]={}
		  }
		  data[`votes`][`${Date.now()}by${user}`]={added:"0",color:`#${color}`,downvoted:"",from:`${from}`,name:`${name}`,time:`${Date.now()}`,upvoted:"",user:`${user}`,vote:"0"}
		  data[`users`][user][`log`][Date.now()]={add:`-10`,name:`${name}`,type:`2`}
		  data[`users`][user][`bal`]=`${Number(data[`users`][user][`bal`])-10}`          
		  res.write("1")
          }
          else{
          	res.write("0")
          }        
        }
        catch{
          console.log(`${err}`)
        }
        return res.end();        
      }
      else if(mode=="totalElements"||path=="totalElements"){
        try{
          res.write(`${data[`elements`].length}`)          
        }
        catch(err){
          console.log(`${err}`)
        }
        return res.end();        
      }
      else if(mode=="totalCombinations"||path=="totalCombinations"){
        try{
          res.write(`${data[`combinations`].length}`)          
        }
        catch(err){
          console.log(`${err}`)
        }
        return res.end(); 
      }
      else if(mode=="updateUnlocked"){  
        try{
          userID=path.substring(0,path.indexOf("/"))
          wdatatest=path.substring(path.indexOf("/")+1) 
          try{
            if(wdatatest<4)
              createUser(userID,data)
            data[`unlocked`][userID].unlocked=`${wdatatest}`
            data[`users`][userID].unlocked=`${wdatatest}`
            res.write(`1`) 
          }  
          catch(err){
            res.write("0")
            data=createUser(userID,data)
            data[`unlocked`][userID].unlocked=`${wdatatest}`
            data[`users`][userID].unlocked=`${wdatatest}`            
            res.write(`1`) 
            console.log(`ERROR: ${err} happened and created new User with userID: ${userID}`)
          }         
        }
        catch(err){
          console.log(`${err}`)
        }
        return res.end();                 
      }
      else if(mode=="leaderboard"||path=="leaderboard"){
        try{
             
          var sorted=[]
          for(id in data[`unlocked`]){
            if(data[`unlocked`][`${id}`][`name`]!="undefined"&&data[`unlocked`][`${id}`][`name`]!=""&&data[`unlocked`][`${id}`][`name`]!=id&&data[`unlocked`][`${id}`][`name`]!=null)
              sorted.push([data[`unlocked`][`${id}`][`name`],data[`unlocked`][`${id}`][`unlocked`]])
            else
              sorted.push(["noShow",data[`unlocked`][`${id}`][`unlocked`]])
          }
          sorted.sort(function(a, b) {return a[1] - b[1];});
          sorted.reverse()
          var jsonout={}
          var i=0
          var added=0
          while(added<50){
            if(sorted[i][0]!="noShow"){
              jsonout[`${sorted[i][0]}`]=`${sorted[i][1]}`
              added++
            }
            i++
          }
          res.write(JSON.stringify(jsonout))     
        }
        catch(err){
          console.log(`${err}`)          
        }
        return res.end();         
      }
      else if(mode=="leaderboardWithID"||path=="leaderboardWithID"){
        try{
             
          var sorted=[]
          for(id in data[`unlocked`]){
            if(data[`unlocked`][`${id}`][`name`]!="undefined"&&data[`unlocked`][`${id}`][`name`]!=""&&data[`unlocked`][`${id}`][`name`]!=id&&data[`unlocked`][`${id}`][`name`]!=null)
              sorted.push([data[`unlocked`][`${id}`][`name`],data[`unlocked`][`${id}`][`unlocked`],`${id}`])
            else
              sorted.push(["noShow",data[`unlocked`][`${id}`][`unlocked`],`${id}`])
          }
          sorted.sort(function(a, b) {return a[1] - b[1];});
          sorted.reverse()
          var jsonout={}
          var i=0
          var added=0
          while(added<50){
            if(sorted[i][0]!="noShow"){
              jsonout[`${sorted[i][2]}`]={"unlocked":`${sorted[i][1]}`,"name":`${sorted[i][0]}`}
              if(data[`users`][`${sorted[i][2]}`][`online`]!=null){
		  if(data[`users`][`${sorted[i][2]}`][`online`]=="1"){
		  	if(Date.now()-Number(data[`users`][`${sorted[i][2]}`][`lastOnline`])<60000)
		  		jsonout[`${sorted[i][2]}`].online="1"
		  	else
		  		jsonout[`${sorted[i][2]}`].online="0"
		  }
		  else
			  jsonout[`${sorted[i][2]}`].online="0"
	      }
	      else
		  jsonout[`${sorted[i][2]}`].online="0"
              added++
            }
            i++
          }
          res.write(JSON.stringify(jsonout))     
        }
        catch(err){
          console.log(`${err}`)          
        }
        return res.end();         
      }
      else if(mode=="updateUsername"){
        try{
          userID=path.substring(0,path.indexOf("/"))
          username=path.substring(path.indexOf("/")+1)
          username=username.split('+').join(' ')
          username=decodeURIComponent(username)
          if(username.length>=5&&username.length<=20){
            contains=false
            for(id in data[`users`]){
              if(data[`users`][id][`name`]==username)
                contains=true
            }
            if(!contains){
              console.log(username)                
              bal=Number(data[`users`][userID][`bal`])
              if(bal>=25&&data[`users`][userID][`name`]!=data[`users`][userID][`uid`]){
                data[`users`][userID][`name`]=`${username}`
                data[`unlocked`][userID][`name`]=`${username}`
                metadata[`userNameToID`][`${username.toLowerCase()}`]=`${userID}`
                data[`users`][userID][`bal`]=`${bal-25}`
                data[`users`][userID][`log`][Date.now()]={add:`-25`,msg:`Display name changed: ${username}`,type:`0`}
                res.write("updated")
              }
              else{
                if(data[`users`][userID][`name`]==data[`users`][userID][`uid`]){
                  data[`users`][userID][`name`]=`${username}`
                  data[`unlocked`][userID][`name`]=`${username}`
                  metadata[`userNameToID`][`${username.toLowerCase()}`]=`${userID}`
                  data[`users`][userID][`log`][Date.now()]={add:`-0`,msg:`Display name changed: ${username}`,type:`0`}
                  res.write("updated")                  
                }
                else
                  res.write("balance")
              }
            }
            else
              res.write("taken")          
          }
          else
            res.write("length")

        }
        catch(err){
          console.log(`${err}`)  
        }
        return res.end();               
      }
      else if(mode=="checkAndCreateNewUser"){
        try{
          userID=path
          try{                
        
              if(data[`users`][userID]==null){
	          data=createUser(userID,data)
                  res.write("1")                
              }
              else{
              	if(data[`users`][userID][`bal`]==null||data[`users`][userID][`elements`]==null||data[`users`][userID][`log`]==null||data[`users`][userID][`name`]==null||data[`users`][userID][`uid`]==null||data[`users`][userID][`unlocked`]==null){
                  data=createUser(userID,data)
                  res.write("1")
                }
                else{
                  res.write("0")
                }
              }              
            }          
            catch(err){
              res.write("0")
              console.log(`ERROR: ${err}`)
            }
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end()                
      }
      else if(mode=="updateVersion"){
        try{
          version=path
          try{                
            data[`version`]=version
            res.write("1")
            fs.writeFileSync('datatest.json',JSON.stringify(data))
          }          
          catch(err){
            res.write("0")
            console.log(`ERROR: ${err}`)
          }
        }
        catch(err){

        }
        return res.end();
      }
      else if(mode=="replaceElementCheck"){
        try{
          fromID=path.substring(0,path.indexOf("/"))
          toID=path.substring(path.indexOf("/")+1) 
          for(comb in data[`combinations`]){
            var from=data[`combinations`][`${comb}`][`from`]
            var temp=data[`combinations`][`${comb}`][`makes`]
            if(data[`combinations`][`${comb}`][`makes`]==fromID){                                           
              res.write(`from ${from} which makes ${temp}\n`)              
            }
            var fromList=from.split(",")
            if(fromList.includes(fromID)){
            	res.write(`Prev: from ${from} which makes ${temp}\n`)
            	fromList=removeA(fromList,fromID)
            	var i=0
            	while(i<fromList.length){
            		if(Number(fromList[i])>toID){
            			break
            		}
            		i++
            	}
            	
            	fromList.splice(i,0,toID)
            	res.write(`Now from ${fromList.join(",")} which makes ${temp}\n`)
            }
          }    

          for(user in data[`users`]){
          var temp=data[`users`][`${user}`][`uid`]
            console.log(`${temp}`)
            elements=""
            if(data[`users`][`${user}`][`elements`]!=null)
              elements = data[`users`][`${user}`][`elements`].split(",")
            else{
              data=createUser(user,data)
              elements = data[`users`][`${user}`][`elements`].split(",")              
            }

            if(elements.includes(fromID)){
              res.write(`prev ${elements}\n`)
              elements=removeA(elements,fromID)
              if(!elements.includes(toID))
                elements.push(toID)
              res.write(`now ${elements}\n\n`)
            }
          }    
 
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();          
      }
      else if(mode=="replaceElement"){
        try{
          fromID=path.substring(0,path.indexOf("/"))
          toID=path.substring(path.indexOf("/")+1)
          for(comb in data[`combinations`]){
            var from=data[`combinations`][`${comb}`][`from`]
            var temp=data[`combinations`][`${comb}`][`makes`]
            if(data[`combinations`][`${comb}`][`makes`]==fromID){             
              data[`combinations`][`${comb}`][`makes`]=`${toID}`
              data[`combination2`][`${from}`]=`${toID}`
            }
            var fromList=from.split(",")
            if(fromList.includes(fromID)){
            	fromList=removeA(fromList,fromID)
            	var i=0
            	while(i<fromList.length){
            		if(Number(fromList[i])>toID){
            			break
            		}
            		i++
            	}
            	
            	fromList.splice(i,0,toID)
            	data[`combinations`][`${comb}`][`from`]=`${fromList.join(",")}`
                data[`combination2`][`${fromList.join(",")}`]=`${temp}`
            	res.write(`Now from ${fromList.join(",")} which makes ${temp}\n`)
            }
          }

          for(user in data[`users`]){
            elements = data[`users`][`${user}`][`elements`].split(",")
            if(elements.includes(fromID)){
              elements=removeA(elements,fromID)
              if(!elements.includes(toID))
                elements.push(toID)
              data[`users`][`${user}`][`elements`]=`${elements.join(",")}`
            }
          }
          res.write("1")
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();        
      }
      else if(mode=="renameElement"){
        try{
          id=path.substring(0,path.indexOf("/"))
          name=path.substring(path.indexOf("/")+1)
          name=name.split('+').join(' ')
          name=decodeURIComponent(name)
          data[`elements`][id][`name`]=`${name}`
          res.write("1")
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();        
      }
      else if(mode=="getCategory"){
        try{
          userID=path.substring(0,path.indexOf("/"))
          elementID=path.substring(path.indexOf("/")+1)
          done=false
          do{
            if(category[`users`]!=null){
              if(category[`users`][`${userID}`]!=null){
                if(category[`users`][`${userID}`][`${elementID}`]!=null){
                  var writeData=category[`users`][`${userID}`][`${elementID}`]
                  res.write(`${writeData}`)
                  done=true
                }
                else
                  category[`users`][`${userID}`][`${elementID}`]=""                    
              }
              else
                category[`users`][`${userID}`]={}
            }
            else
              category[`users`]={}
          }while(!done)                    
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="setCategory"){
        try{
          userID=path.substring(0,path.indexOf("/"))
          path=path.substring(path.indexOf("/")+1)
          elementID=path.substring(0,path.indexOf("/"))      
          newCategory=path.substring(path.indexOf("/")+1) 
          newCategory=newCategory.split('+').join(' ')
          newCategory=decodeURIComponent(newCategory)
          done=false
          do{
            if(category[`users`]!=null){
              if(category[`users`][`${userID}`]!=null){
                if(category[`users`][`${userID}`][`${elementID}`]!=null){
                  category[`users`][`${userID}`][`${elementID}`]=`${newCategory}`
                  res.write("1")
                  done=true
                }
                else
                  category[`users`][`${userID}`][`${elementID}`]=""                    
              }
              else
                category[`users`][`${userID}`]={}
            }
            else
              category[`users`]={}
          }while(!done)
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();            
      }
      else if(mode=="getRecommendedCategory"){
        try{
          elementID=path
          count={}
          if(category[`users`]!=null){
            for(user in category[`users`]){
              if(category[`users`][`${user}`][`${elementID}`]!=null){
                categories=category[`users`][`${user}`][`${elementID}`].split(",")                
                for(cat in categories){
                  if(count[`${categories[cat]}`]!=null){
                    var tempNumber=Number(count[`${categories[cat]}`])+1
                    count[`${categories[cat]}`]=`${tempNumber}`
                  }
                  else
                    count[`${categories[cat]}`]="0"
                }
              }
            }
          }
          else
            category[`users`]={}    
          maxCountedCat=""            
          for(cat in count){
              if(maxCountedCat=="")
                maxCountedCat=cat                
              if(count[`${cat}`]>count[`${maxCountedCat}`])
                maxCountedCat=cat
            }
          
          res.write(`${maxCountedCat}`)
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();        
      }
      else if(mode=="getSettedCategory"){
        try{
          userID=path
          setted=""
          if(category[`users`][`${userID}`]!=null){
            for(elementID in category[`users`][`${userID}`]){
                if(setted=="")
                  setted=`${elementID}`
                else
                  setted=`${setted},${elementID}`
              }
          }
          else
            category[`users`][`${userID}`]={}    
          
          res.write(`${setted}`)
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();       
      }
      else if(mode=="getAllCategory"){
        try{
          userID=path
          datatest={}
          if(category[`users`][`${userID}`]!=null){
            for(elementID in category[`users`][`${userID}`]){
                datatest[`${elementID}`]=category[`users`][`${userID}`][`${elementID}`]
            }            
          }
          else
            category[`users`][`${userID}`]={}    
          
          res.write(JSON.stringify(datatest))
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();        
      }
      else if(mode=="convertAllLogToObject"||path=="convertAllLogToObject"){
        try{
          for(user in data[`users`]){
              if(JSON.stringify(data[`users`][`${user}`][`log`]).substring(0,1)=="["){
                //This was a one-time use function which was made because some user were having logs as array instead of object, to fix it this function was made
                //If you encounter this ever again, just uncomment the below part
                //data[`users`][`${user}`][`log`]=Object.assign({},data[`users`][`${user}`][`log`])
                //res.write(`${JSON.stringify(data[`users`][`${user}`][`log`])} \n`)
                //res.write(`${JSON.stringify(Object.assign({},data[`users`][`${user}`][`log`]))}\n\n`)
              }
            }  
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();       
      }     
    else if(mode=="refer"){
        try{
          userID=path.substring(0,path.indexOf("/"))
          referID=path.substring(path.indexOf("/")+1)
          if(`${userID}`!=`${referID}`){
            if(refer[`to`][`${userID}`]==null){
              if(data[`users`][`${referID}`]!=null){
                refer[`to`][`${userID}`]=referID
                data[`users`][`${userID}`][`log`][`${Date.now()}`]={add : "+150",msg : `Referral reward;Reward for referring to u/${referID}/`,type : "0"}
                data[`users`][`${referID}`][`log`][`${Date.now()}`]={add : "+150",msg : `Referral reward;u/${userID}/ referred using your referral code`,type : "0"}
                var username =data[`users`][userID][`name`]
                sendNotification(referID,"New refer using you code",`${username} used your referral code, you got 150 💵`)
                ubal=data[`users`][userID][`bal`]
                rbal=data[`users`][referID][`bal`]
                data[`users`][userID][`bal`]=`${Number(ubal)+150}`
                data[`users`][referID][`bal`]=`${Number(rbal)+150}`
                if(refer[`from`][`${referID}`]==null)
                  refer[`from`][`${referID}`]=`${userID}`
                else{
                  currentRef = refer[`from`][`${referID}`]
                  refer[`from`][`${referID}`]=`${currentRef},${userID}`
                }
                res.write("referred")        
              } 
              else
                res.write("invalid")  
            }
            else
              res.write("already") 
          }
          else
            res.write("invalid")
                    
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="getRefer"){
        try{
          userID=path          
          if(refer[`from`][`${userID}`]!=null){
             res.write(`${refer[`from`][userID]}`)
          }
          else
            res.write("")           
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="getReferStatus"){
        try{
          userID=path          
          if(refer[`to`][`${userID}`]==null){
             res.write("0")
          }
          else
            res.write("1")           
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="makeRedeem"){
        try{
          code=path.substring(0,path.indexOf("/"))
          path=path.substring(path.indexOf("/")+1)
          type=path.substring(0,path.indexOf("/"))
          path=path.substring(path.indexOf("/")+1)
          amount=path.substring(0,path.indexOf("/"))
          msg=path.substring(path.indexOf("/")+1)
          msg=msg.split('+').join(' ')
          msg=decodeURIComponent(msg)
          if(redeem[`${code}`]==null){
            redeem[`${code}`]={}
            redeem[`${code}`][`type`]=`${type}`
            redeem[`${code}`][`amount`]=`${amount}`
            redeem[`${code}`][`msg`]=`${msg}`
            res.write("added")
          }
          else{
            res.write("Redeem code with same code already exist")
          }                    
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="redeem"){
        try{
          userID=path.substring(0,path.indexOf("/"))
          code=path.substring(path.indexOf("/")+1)
          if(redeem[`${code}`]!=null){
            if(redeem[`${code}`][`type`]=="single"){
              if(redeem[`${code}`][`users`]==null){
                redeem[`${code}`][`users`]=`${userID}`

                ubal=data[`users`][userID][`bal`] 
                addBal=redeem[`${code}`][`amount`]
                
                if(redeem[`${code}`][`msg`]=="default")
                  data[`users`][`${userID}`][`log`][`${Date.now()}`]={add : `+${addBal}`,msg : `Redeem reward;Using code ${code}`,type : "0"}  
                else
                  data[`users`][`${userID}`][`log`][`${Date.now()}`]={add : `+${addBal}`,msg : redeem[`${code}`][`msg`],type : "0"}                        
                data[`users`][userID][`bal`]=`${Number(ubal)+Number(addBal)}`

                res.write("claimed")
              }
              else
                res.write("already")
            }
            if(redeem[`${code}`][`type`]=="multiple"){
              if(redeem[`${code}`][`users`]==null){
                redeem[`${code}`][`users`]=`${userID}`

                ubal=data[`users`][userID][`bal`] 
                addBal=redeem[`${code}`][`amount`]
		
		
                if(redeem[`${code}`][`msg`]=="default")
                  data[`users`][`${userID}`][`log`][`${Date.now()}`]={add : `+${addBal}`,msg : `Redeem reward;Using code ${code}`,type : "0"}  
                else
                  data[`users`][`${userID}`][`log`][`${Date.now()}`]={add : `+${addBal}`,msg : redeem[`${code}`][`msg`],type : "0"}                              
                data[`users`][userID][`bal`]=`${Number(ubal)+Number(addBal)}`

                res.write("claimed")
              }
              else{
                userClaimed=redeem[`${code}`][`users`].split(",")
                if(userClaimed.includes(`${userID}`))
                  res.write("already")
                else{
                  userClaimed.push(`${userID}`)
                  ubal=data[`users`][userID][`bal`] 
                  addBal=redeem[`${code}`][`amount`]

                  if(redeem[`${code}`][`msg`]=="default")
                    data[`users`][`${userID}`][`log`][`${Date.now()}`]={add : `+${addBal}`,msg : `Redeem reward;Using code ${code}`,type : "0"}  
                  else
                    data[`users`][`${userID}`][`log`][`${Date.now()}`]={add : `+${addBal}`,msg : redeem[`${code}`][`msg`],type : "0"}                              
                  data[`users`][userID][`bal`]=`${Number(ubal)+Number(addBal)}`

                  res.write("claimed")
                  redeem[`${code}`][`users`]=userClaimed.join(",")
                }
              }
            }
            
          } 
          else
            res.write("invalid")         
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="setElementNameToID"){
      	metadata[`elementNameToID`]={"fire":"0","earth":"1","water":"2","air":"3"}
      	for(combination in data[`combination2`]){
      		var elementID=data[`combination2`][`${combination}`]
      		var elementName=data[`elements`][`${elementID}`][`name`].toLowerCase()
      		if(metadata[`elementNameToID`][`${elementName}`]==null){      			
			metadata[`elementNameToID`][`${elementName}`]=`${elementID}`
			res.write(`inserted ${elementID}:${elementName}\n`)
      		}
      		else{
      			var metadataname=metadata[`elementNameToID`][`${elementName}`]
      			if(metadataname!=elementID)
	      			res.write(`duplicateAt ${elementID}:${elementName}, metadata:${metadataname}\n`)
      		}
      	}
      	return res.end();
      }
      else if(mode=="setUserNameToID"){
      	metadata[`userNameToID`]={}
      	for(user in data[`users`]){
      		var userName=data[`users`][user][`name`].toLowerCase()
      		res.write(`${userName}:${user}`)
      		metadata[`userNameToID`][`${userName}`]=`${user}`
      	}
      	return res.end();
      }
      else if(mode=="getPlayerInfo"){
        try{
          userID=path          
          var info={}
          
          //Name
          info.name=data[`users`][userID][`name`]
          
          //Balance
          if(data[`users`][userID][`private`]!=null){
          	if(data[`users`][userID][`private`]=="1")
          		info.bal=data[`users`][userID][`bal`]
          	else
          		info.bal="Private"	
          }
          else
          	info.bal="Private"
          
          //Status		
          if(data[`users`][userID][`status`]!=null)   
          	info.status=data[`users`][userID][`status`]
          else
          	info.status="No status"
          
          
         //rank	
          var sorted=[]
          for(id in data[`unlocked`]){
            if(data[`unlocked`][`${id}`][`name`]!="undefined"&&data[`unlocked`][`${id}`][`name`]!=""&&data[`unlocked`][`${id}`][`name`]!=null&&data[`unlocked`][`${id}`][`name`]!=id)
              sorted.push([id,data[`unlocked`][`${id}`][`unlocked`]])            
          }
          sorted.sort(function(a, b) {return a[1] - b[1];});
          sorted.reverse()
          var i=0
          var rank=0
          var prevU=0
          for(each in sorted){
          	if(prevU!=sorted[i][1])
          		rank++
          	prevU=sorted[i][1]
          	if(userID==sorted[i][0])
          		break
          	i++
          }
          info.rank=rank.toString()
          
          //Elements Unlocked
	  info.unlocked = sorted[i][1].toString()
          
          //Elements created
          count=0
          for(each in data.elements){
          	if(data[`elements`][each][`by`]==userID)
          		count++
          }
          info.elementsCreated=count.toString()          
          
          //Setting online offline status	  
	  if(data[`users`][userID][`online`]!=null){
	  	if(data[`users`][userID][`online`]=="1"){
	  		console.log(Date.now()-Number(data[`users`][userID][`lastOnline`]))
	  		if(Date.now()-Number(data[`users`][userID][`lastOnline`])<60000)
	  			info.online="1"
	  		else
	  			info.online="0"
	  	}
	  	else
		  	info.online=data[`users`][userID][`online`]
	  }
	  else
	  	info.online="0"
	  if(data[`users`][userID][`lastOnline`]!=null)
	  	info.lastOnline=data[`users`][userID][`lastOnline`]
	  else
	  	info.lastOnline="0"
	  
	  
	  
          res.write(JSON.stringify(info))	  
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="setOnline"){
        try{
          userID=path          
          data[`users`][userID][`online`]="1"
          data[`users`][userID][`lastOnline`]=Date.now().toString()
          res.write("1")           
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="setOffline"){
        try{
          userID=path          
          data[`users`][userID][`online`]="0"
          data[`users`][userID][`lastOnline`]=Date.now().toString()
          res.write("1")           
        }
        catch(err){
          console.log(`ERROR: ${err}`)
        }
        return res.end();           
      }
      else if(mode=="getLog"){               
        try{
          userID=path.substring(0,path.indexOf("/"))
          amount=Number(path.substring(path.indexOf("/")+1))
          keys=Object.keys(data[`users`][userID][`log`])
	  lengthLog=keys.length
	  console.log(amount+"and"+lengthLog)
	  if(lengthLog<amount){
	  	amount=lengthLog	
	  }
	  i=lengthLog-1
	  var jsonout={}
	  while(i>=lengthLog-amount){
	  	jsonout[keys[i]]=data[`users`][userID][`log`][keys[i]]	  	
	  	i--
	  }
          res.write(JSON.stringify(jsonout))           
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
    
    else if(mode=="getPrivacy"){               
        try{
          userID=path
          if(data[`users`][userID][`private`]!=null)
          	res.write(data[`users`][userID][`private`])          
          else
          	res.write("0")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
    
    else if(mode=="setPrivacy"){               
        try{
	  userID=path.substring(0,path.indexOf("/"))
          privacy=Number(path.substring(path.indexOf("/")+1))
          data[`users`][userID][`private`]=`${privacy}`
          res.write("1")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
    
    else if(mode=="getStatus"){               
        try{
          userID=path
          if(data[`users`][userID][`status`]!=null)
          	res.write(data[`users`][userID][`status`])          
          else
          	res.write("")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
    
    else if(mode=="setStatus"){               
        try{
	  userID=path.substring(0,path.indexOf("/"))
          status=path.substring(path.indexOf("/")+1)
          status=status.split('+').join(' ')
          status=decodeURIComponent(status)
          if(status.length>64)
          	status=status.substring(0,63)
          data[`users`][userID][`status`]=`${status}`
          res.write("1")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="addElementComment"){               
        try{
	  userID=path.substring(0,path.indexOf("/"))
	  path=path.substring(path.indexOf("/")+1)
          element=path.substring(0,path.indexOf("/"))
          comment=path.substring(path.indexOf("/")+1)
          comment=comment.split('+').join(' ')
          comment=decodeURIComponent(comment)
          currentBal=Number(data[`users`][userID][`bal`])       
          if(currentBal>=10){
		  if(comment.length>0){
		  	if(comment.length<128){
		  		if(comments[element]==null)
		  			comments[element]={}
		  		comments[element][Date.now()]={"user":userID,"msg":comment,likes:0} 
		  		elementName=data[`elements`][element][`name`]
		  		data[`users`][userID][`bal`]=currentBal-10
		  		data[`users`][userID][`log`][Date.now()]={add:`-10`,msg:`Wrote a comment;You wrote a comment on ${elementName} saying ${comment}`,type:`0`}
		  		res.write("1");         		
		  	}
		  	else
		  		res.write("long");
		  }
		  else
		  	res.write("empty");
          }
          else
          	res.write("bal")
          
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="getElementComment"){               
        try{
	  element=path
	  if(comments[element]!=null)
          	res.write(JSON.stringify(comments[element]))   
          else
          	res.write("{}")                
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="removeElementComment"){               
        try{
	  by=path.substring(0,path.indexOf("/"))
	  path=path.substring(path.indexOf("/")+1)
          element=path.substring(0,path.indexOf("/"))
          commentID=path.substring(path.indexOf("/")+1)
          activeMods=mods[`0`].split(",")
          
          if(comments[element][commentID]!=null){
	    userID=comments[element][commentID][`user`]
	    if(activeMods.includes(by)||by==userID){
		elementName=data[`elements`][element][`name`]
	    	if(by!=userID){
          		modName=data[`users`][by][`name`]          		
          		comment=comments[element][commentID][`msg`]
          		bot.channels.cache.get('742724997889523793').send(modName+" removed comment ["+comment+"] on element "+elementName)
          	}
	    
          	comments[element][commentID]={"user":userID,"msg":"[Deleted comment]",likes:0} 
          	data[`users`][userID][`log`][Date.now()]={add:`-0`,msg:`Violation;Your comment on ${elementName} got removed. This game is 13+ and aimed at general public, so please be humane while writing comments.`,type:`0`}
		res.write("1")
	    }
	    else
	  	res.write("mod");
	    
	  }
	  else{
	        res.write("invalid")
	  }
          
          
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="resetUsername"){               
        try{
	  by=path.substring(0,path.indexOf("/"))
	  userID=path.substring(path.indexOf("/")+1)
          activeMods=mods[`0`].split(",")
          if(activeMods.includes(by)){
          	newName=Date.now()	
	  	bot.channels.cache.get('742724997889523793').send(data[`users`][by][`name`]+' removed the username of '+data[`users`][userID][`name`])
  	  	data[`users`][userID][`name`]=`${newName}`
	  	data[`unlocked`][userID][`name`]=`${newName}`
	  	metadata[`userNameToID`][`${newName}`]=`${userID}`
	  	data[`users`][userID][`log`][Date.now()]={add:`-0`,msg:`Violation;Display name removed by mods. This game is 13+ and aimed at general public, so please be humane while selecting display name.`,type:`0`}
	  	res.write("1")
          }
          else
          	res.write("mod")
          
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="reportUsername"){               
        try{	  
	  by=path.substring(0,path.indexOf("/"))
	  userID=path.substring(path.indexOf("/")+1)  
	  userName=data[`users`][by][`name`]
	  reportedName=data[`users`][userID][`name`]                
          bot.channels.cache.get('742724997889523793').send('New report on a user name\n'+userName+' reported the username '+reportedName)
          res.write("1")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="addMod"){
      	try{
	  userID=path.substring(0,path.indexOf("/"))
	  level=path.substring(path.indexOf("/")+1)
	  if(mods==null)
	  	mods={}
          if(mods[level]==null){
          	mods[level]=userID
          	res.write("added")
          }
          else{
          	activeMods=mods[level].split(",")
          	if(activeMods.includes(userID)){          		
          		res.write("already")
          	}
          	else{
          		mods[level]+=","+userID
          		res.write("added")
          	}          	
          }
          	
          
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="reportVoteComment"){
      	try{
	  userID=path.substring(0,path.indexOf("/"))
	  path=path.substring(path.indexOf("/")+1)
          voteID=path.substring(0,path.indexOf("/"))      
          commentID=path.substring(path.indexOf("/")+1)	
	  userName=data[`users`][userID][`name`]    
	  voteName=data[`votes`][voteID][`name`]
	  commentBy=data[`votes`][voteID][`comments`][commentID][`uid`]
	  commentBy=data[`users`][commentBy][`name`]
	  commentMsg=data[`votes`][voteID][`comments`][commentID][`msg`]
	  bot.channels.cache.get('742724997889523793').send('New report on a comment\n'+userName+' reported the comment\n```'+commentBy+' : '+commentMsg+'``` on vote '+voteName)
          res.write("1")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="reportElementComment"){
      	try{
	  userID=path.substring(0,path.indexOf("/"))
	  path=path.substring(path.indexOf("/")+1)
          elementID=path.substring(0,path.indexOf("/"))      
          commentID=path.substring(path.indexOf("/")+1)	
	  userName=data[`users`][userID][`name`]    
	  elementName=data[`elements`][elementID][`name`]
	  commentBy=comments[elementID][commentID][`user`]
	  commentBy=data[`users`][commentBy][`name`]
	  commentMsg=comments[elementID][commentID][`msg`]
	  bot.channels.cache.get('742724997889523793').send('New report on a comment\n'+userName+' reported the comment\n```'+commentBy+' : '+commentMsg+'``` on element '+elementName)
          res.write("1")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="removeMod"){
      	try{
	  userID=path.substring(0,path.indexOf("/"))
	  level=path.substring(path.indexOf("/")+1)
	  
          if(mods[level]!=null){
          	activeMods=mods[level].split(",")
          	if(activeMods.includes(userID)){
          		activeMods=removeA(activeMods,userID)
          		mods[level]=activeMods.join(",")
          		res.write("removed")
          	}
          	else
          		res.write("invalid")
          }
          else
          	res.write("invalid")
          	
          
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="isMod"){
      	try{
	  userID=path.substring(0,path.indexOf("/"))
	  level=path.substring(path.indexOf("/")+1)
	  
          if(mods[level]!=null){
          	activeMods=mods[level].split(",")
          	if(activeMods.includes(userID)){          		
          		res.write("1")
          	}
          	else
          		res.write("0")
          }
          else
          	res.write("0")
          	
          
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="setFCMToken"){               
        try{
	  var userID=path.substring(0,path.indexOf("/"))
          var token=path.substring(path.indexOf("/")+1)
          token=token.split('+').join(' ')
          token=decodeURIComponent(token)
          if(metadata[`fcmtoken`]==null){
          	metadata[`fcmtoken`]={}
          }
          metadata[`fcmtoken`][userID]=`${token}`
          res.write("1")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }    
    else if(mode=="sendNotification"){ 
                 
        try{
	  var userID=path.substring(0,path.indexOf("/"))
	  path=path.substring(path.indexOf("/")+1)
          var title=path.substring(0,path.indexOf("/"))
          var msg=path.substring(path.indexOf("/")+1)
          title=title.split('+').join(' ')
          title=decodeURIComponent(title)  
          msg=msg.split('+').join(' ')
          msg=decodeURIComponent(msg)          
          res.write(sendNotification(userID,title,msg)) 
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="removeAccount"){ 
                 
        try{
	  var userID=path.substring(0,path.indexOf("/"))
	  var confirm=path.substring(path.indexOf("/")+1)
          if(confirm==userID){
          	delete data[`users`][userID]
          	res.write("deleted")
          }       
          else{
          	res.write(JSON.stringify(data[`users`][userID]))
          }             
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="getElements"){                 
        try{
          var from=0
          var to=0
          if(path.indexOf("/")!=-1){
	  	from=Number(path.substring(0,path.indexOf("/")))
	  	to=Number(path.substring(path.indexOf("/")+1))
	  }
	  else
	  	from=Number(path)
          
          if(to==0)
          	to=data[`elements`].length
          var temp={}
          var i=from
          while(i<to){
          	temp[`${i}`]=data[`elements`][`${i}`]
          	i++
          }
          res.write(JSON.stringify(temp))                  
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="getCombinations"){                 
        try{
          var from=0
          var to=0
          if(path.indexOf("/")!=-1){
	  	from=Number(path.substring(0,path.indexOf("/")))
	  	to=Number(path.substring(path.indexOf("/")+1))
	  }
	  else
	  	from=Number(path)
          
          if(to==0)
          	to=data[`combinations`].length
          var temp={}
          var i=from
          while(i<to){
          	temp[`${i}`]=data[`combinations`][`${i}`]
          	i++
          }
          res.write(JSON.stringify(temp))                  
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="replaceCombinationCheck"){
        try{
          oldFrom=path.substring(0,path.indexOf("/"))
          path=path.substring(path.indexOf("/")+1)
          oldMakes=path.substring(0,path.indexOf("/"))          
          path=path.substring(path.indexOf("/")+1)
          newFrom=path.substring(0,path.indexOf("/"))          
          newMakes=path.substring(path.indexOf("/")+1)
          
          
          var i=0
          var len = data[`combinations`].length
          while(i<len){
          	if(data[`combinations`][i][`from`]==oldFrom&&data[`combinations`][i][`makes`]==oldMakes)
          		break
          	i++
          }
          if(i!=len){
          	res.write(`Found at ${i}\n\n`)
          	res.write(`old : ${oldFrom} = ${oldMakes}\nNew : ${newFrom} = ${newMakes}\n\n`)
          	res.write(`Comb2 old: ${oldFrom} = ${data[`combination2`][oldFrom]}\n`) 
          	var j=0		
		while(j<len){
			if(data[`combinations`][j][`from`]==oldFrom&&data[`combinations`][j][`makes`]!=oldMakes)
				break
			j++
		}
		if(j!=len){
			res.write(`Changing old Comb2 makes to ${data[`combinations`][j][`makes`]} and also making new Comb2`)
		}
		else
			res.write("No other combination with same from found, hence deleting old Comb2 and making new")
          }
          else
          	res.write("No such combination found")
        }
        catch{
          console.log(`ERROR: ${err}`)
        }                 
        return res.end();
      }
      else if(mode=="replaceCombination"){
        try{
          oldFrom=path.substring(0,path.indexOf("/"))
          path=path.substring(path.indexOf("/")+1)
          oldMakes=path.substring(0,path.indexOf("/"))          
          path=path.substring(path.indexOf("/")+1)
          newFrom=path.substring(0,path.indexOf("/"))          
          newMakes=path.substring(path.indexOf("/")+1)
          
          
          var i=0
          var len = data[`combinations`].length
          while(i<len){
          	if(data[`combinations`][i][`from`]==oldFrom&&data[`combinations`][i][`makes`]==oldMakes)
          		break
          	i++
          }
          if(i!=len){
          	res.write(`Found at ${i}\n\n`)
          	data[`combinations`][i][`from`]=newFrom
          	data[`combinations`][i][`makes`]=newMakes
          	res.write(`old DONE: ${oldFrom} = ${oldMakes}\nNew : ${newFrom} = ${newMakes}\n\n`)
          	res.write(`Comb2 old: ${oldFrom} = ${data[`combination2`][oldFrom]}\n`) 
          	var j=0		
		while(j<len){
			if(data[`combinations`][j][`from`]==oldFrom&&data[`combinations`][j][`makes`]!=oldMakes)
				break
			j++
		}
		if(j!=len){
			data[`combination2`][oldFrom]=data[`combinations`][j][`makes`]
			data[`combination2`][newFrom]=`${newMakes}`
			res.write(`Changing old Comb2 makes to ${data[`combinations`][j][`makes`]} and also making new Comb2`)
		}
		else{
			if(data[`combination2`][oldFrom]!=null)
				delete data[`combination2`][oldFrom]
			data[`combination2`][newFrom]=`${newMakes}`
			res.write("No other combination with same from found, hence deleting old Comb2 and making new")
		}
		res.write("\n\nMETADATA\n--------------------------------\n")
		for(user in data[`users`]){
			if(metadata[`replacedCombination`]==null)
				metadata[`replacedCombination`]={}
			if(metadata[`replacedCombination`][user]==null)
				metadata[`replacedCombination`][user]=`${i}`
			else{
				var tempD = metadata[`replacedCombination`][user]
				metadata[`replacedCombination`][user]=`${tempD},${i}`
			}
			res.write(`${user} : ${metadata[`replacedCombination`][user]}\n`)
		}
		res.write(`--------------------------------`)
          }
          else
          	res.write("No such combination found")
        }
        catch(e){
          console.error(e)
        }                 
        return res.end();
      }
      else if(mode=="updateElementName"){
        try{
          id=path.substring(0,path.indexOf("/"))                 
          newName=path.substring(path.indexOf("/")+1)
          newName=newName.split('+').join(' ')
          newName=decodeURIComponent(newName)
          data[`elements`][id][`name`]=newName
        }
        catch(e){
          console.error(e)
        }                 
        return res.end();
      }
      else if(mode=="updateVoteName"){
        try{
          id=path.substring(0,path.indexOf("/"))                 
          newName=path.substring(path.indexOf("/")+1)
          newName=newName.split('+').join(' ')
          newName=decodeURIComponent(newName)
          data[`votes`][id][`name`]=newName
        }
        catch(e){
          console.error(e)
        }                 
        return res.end();
      }
      else{
      	res.write("invalid req");
      	res.end();
      }
    }
    catch(err){
      console.log(`ERROR: ${err}`)
      return res.end();
    }

    
}).listen(8085,'0.0.0.0');
// Create an instance of the http server to handle HTTP requests

function createUser(userID,data){
  data[`users`][userID]={bal:"50",elements:"0,1,2,3",log:{},name:`${userID}`,uid:`${userID}`,unlocked:"4"}
  data[`unlocked`][userID]={name:`${userID}`,unlocked:"4"}
  data[`users`][userID][`log`][`${Date.now()}`]={add : "+50",msg : "Welcome to the game!",type : "0"}
  return data
}

function removeA(arr,str) {
    var pos=arr.indexOf(str)
    if(pos>-1)
      arr.splice(pos,1)
    return arr;
}

function sendNotification(userID,title,message){
	if(metadata[`fcmtoken`][userID]!=null){
		var registrationToken=metadata[`fcmtoken`][userID]
		const messages = [];
		messages.push({
		  notification: {title: `${title}`, body: `${message}`},
		  token: registrationToken,
		});
		admin.messaging().sendAll(messages)
		  .then((response) => {
		    console.log(response.successCount + ' messages were sent successfully');
		  });
		return 1;
	}
	return 0;
}

function getdata(path){
  gpath=path.split("/")
  var gdata=data
  for(each in gpath){    
    gdata=gdata[`${gpath[`${each}`]}`]
  }
  return gdata
}
