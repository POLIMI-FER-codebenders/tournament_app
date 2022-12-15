
import "../styles/iframe.css"
import React from 'react';
import { getData } from "../utils";
import { GoToErrorPage } from "../utils";
import { useLocation } from 'react-router-dom';
import clock from '../images/clock.png';

export function CDFrame(props) {

  const location = useLocation();
  /*  if(location.state==null) {
     return (<div>
     <p> you are not inside any game</p> 
     </div>);
     }else {
        let cdurl= location.state.info.server + "/login?token=" +  location.state.info.token;
          return  <iframe id="iframe" src={cdurl} title="cdtournamentsgames" allowFullScreen ></iframe>
            
    }
    */
  return (
    <div id="maindiv">
      
         <div id="framemenu">
          <a href="http://localhost:3000" id="backlink">Back To Tournament Application</a>
          <img id="clock" src={clock}></img>
          <p id="time" >Times</p>
        
      </div>
      <div id="iframediv">
        <iframe id="iframe" src="https://code-defenders.org" title="cdtournamentsgames" ></iframe>

      </div>
    </div>

  );
}





