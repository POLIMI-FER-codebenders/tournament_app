import { useEffect } from "react";
import { useRouteError } from "react-router-dom";
import { useLocation } from 'react-router-dom';
import { useNavigate } from "react-router-dom";
import { getData } from "../utils.js"
import React, { useState } from 'react';
import '../styles/Streaming.css';
import cdlogo from '../images/cdlogo.png';
import background from '../images/background1.jpg';
import SockJsClient from 'react-stomp';
import { EventEntry } from "./EventEntry.js";
const SOCKET_URL = process.env.REACT_APP_BACKEND_ADDRESS + '/watch';
export default function Streaming(props) {
    const [attackersPoints, setAttackersPoints] = useState(0);
    const [defendersPoints, setDefendersPoints] = useState(0);
    const [events, setEvents] = useState([]);
    const[trigger,SetTrigger]=useState(false);
    const location = useLocation();
    let navigate = useNavigate();
    function RetrievePhase(){
        if(location.state.info.status==="CREATED") return "Match in break time";
        else if(location.state.info.status==="IN_PHASE_ONE") return "Match in phase one";
        else if(location.state.info.status==="IN_PHASE_TWO") return "Match in phase two";
        else if(location.state.info.status==="IN_PHASE_THREE") return "Match in phase three";
        else if(location.state.info.status==="ENDED") return "Match Ended";
        else return location.state.info.status;
    }
     
    useEffect(() => {
        let page = document.getElementsByTagName("body")[0];
        page.className="backgroundstreaming";
    
        if (location.state === null) navigate("/");
        else {

            getData("/streaming/score?matchId=" + location.state.info.id).then((response) => {
                if (response.status === 200) {
                    setAttackersPoints(response.result.attackersScore * 1);  //converting string to integer
                    setDefendersPoints(response.result.defendersScore * 1);
                    location.state.info.status=response.result.status;
                    
                        /*            
                    let testevententry = {
                        attackersScore: null, defendersScore: null, timestamp: 1671560242,
                        type: "DEFENDER_TEST_CREATED", user: "y99"
                    }
                    let testevent=[];
                    for(let i = 0; i < 8; i++){
                        testevent.push(testevententry);
                    }
                    setEvents(testevent);
                    */

                }
                else navigate("/error", { state: { message: response.message } });
            })
        }
        return function cleanup() {
            let page = document.getElementsByTagName("body")[0];
            page.className="";
          }
    }, [])
    let onConnected = () => {
        console.log("Connected!!");
    }

    let OnMessageReceived = (msg) => {
        
        
        if(msg.type==="SCORE_UPDATE"){
            setAttackersPoints(msg.attackersScore);
            setDefendersPoints(msg.defendersScore);
        }
        if(msg.type==="GAME_STARTED") location.state.info.status="IN_PHASE_ONE";
        if(msg.type==="GAME_GRACE_ONE") location.state.info.status="IN_PHASE_TWO";
        if(msg.type==="GAME_GRACE_TWO") location.state.info.status="IN_PHASE_THREE";
        if(msg.type==="GAME_FINISHED") location.state.info.status="ENDED"
        if(msg.type!=="SCORE_UPDATE"){
        let eventscopy = events;
        eventscopy.push(msg);
        setEvents(eventscopy);
        setTimeout(()=>{var e=document.getElementById("eventsdiv");
        e.scrollTop=e.scrollHeight;
        },100);
        }
    }
    let frontendAddress = process.env.REACT_APP_FRONTEND_ADDRESS;
    return (
        <>
            <SockJsClient
                url={SOCKET_URL}
                topics={['/live/' + location.state.info.id]}
                onConnect={onConnected}
                onDisconnect={console.log("Disconnected!")}
                onMessage={msg =>{
                     OnMessageReceived(msg);
                     SetTrigger(!trigger);
                } 
                }
                debug={false}
            />
            <div id="streamingheader">
                <div id="backsheader">
            <a href={frontendAddress} id="backlinkstreaming">Back To Tournament Application</a>
            </div>
                <div id="phaseheader">{RetrievePhase()}</div>
                </div>
            <div id="scorediv">
                <div id="firstteam" >
                    <div className="streamingheading">{location.state.info.attackersTeam.name}</div>
                    <div className="streamingscore">{attackersPoints}</div>
                </div>
                <div id="middleteamzone" >
                    <img id="cdlogo" src={cdlogo}></img>
                </div>
                <div id="secondteam">
                    <div className="streamingheading">{location.state.info.defendersTeam.name}</div>
                    <div className="streamingscore">{defendersPoints}</div>
                </div>
            </div>
            <div
                id="eventsdiv">

                {events.map((object, i) => <EventEntry record={object} key={i}
                />)}
            </div>
        </>
    )

}