
import "../styles/iframe.css"
import React from 'react';
import { getData } from "../utils";
import { GoToErrorPage } from "../utils";
import {useLocation} from 'react-router-dom';

export function CDFrame(props) {

    const location = useLocation();
    if(location.state==null) {
     return (<div>
     <p> you are not inside any game</p> 
     </div>);
     }else {
        let cdurl= location.state.info.server + "/login?token=" +  location.state.info.token;
          return  <iframe id="iframe" src={cdurl} title="cdtournamentsgames" allowFullScreen ></iframe>
            
    }
}




