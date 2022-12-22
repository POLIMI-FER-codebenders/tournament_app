import { useEffect } from "react";
import { useRouteError } from "react-router-dom";
import { useLocation } from 'react-router-dom';
import { useNavigate } from "react-router-dom";
import { getData } from "../utils.js"
import React, { useState } from 'react';
import '../styles/Streaming.css';
import cdlogo from '../images/cdlogo.png';
import SockJsClient from 'react-stomp';
import { EventEntry } from "./EventEntry.js";
const SOCKET_URL = '/watch';
export default function Streaming(props) {
    const [attackersPoints, setAttackersPoints] = useState(0);
    const [defendersPoints, setDefendersPoints] = useState(0);
    const [events, setEvents] = useState([]);
    const[trigger,SetTrigger]=useState(false);
    const location = useLocation();
    let navigate = useNavigate();


    useEffect(() => {
        console.log(location.state);
        if (location.state === null) navigate("/");
        else {

            getData("/streaming/score?matchId=" + location.state.info.id).then((response) => {
                if (response.status === 200) {
                    setAttackersPoints(response.result.attackersScore);
                    setDefendersPoints(response.result.defendersScore);
                    /*
                    let testevententry = {
                        attackersScore: null, defendersScore: null, timestamp: 1671560242,
                        type: "DEFENDER_TEST_CREATED", user: "y99"
                    }
                    let testevent=[];
                    for(let i = 0; i < 2; i++){
                        testevent.push(testevententry);
                    }
                    setEvents(testevent);
                    */

                }
                else navigate("/error", { state: { message: response.message } });
            })
        }
    }, [])
    let onConnected = () => {
        console.log("Connected!!")
    }

    let OnMessageReceived = (msg) => {
        
        if(msg.type==="SCORE_UPDATE"){
            setAttackersPoints(msg.attackersScore);
            setDefendersPoints(msg.defendersScore);
        }
        let eventscopy = events;
        if (eventscopy.length == 10) eventscopy.pop();

        console.log(eventscopy);
        eventscopy.push(msg);
        setEvents(eventscopy)
        console.log(msg);
        console.log(events);
   

    }

    console.log('/live/' + location.state.info.id);
    return (
        <>
            <SockJsClient
                url={SOCKET_URL}
                topics={['/live/' + location.state.info.id]}
                onConnect={onConnected}
                onDisconnect={console.log("Disconnected!")}
                onMessage={msg =>{
                     OnMessageReceived(msg) 
                     SetTrigger(!trigger);
                } 
                }
                debug={false}
            />
            <div id="streamingheader"></div>
            <div id="scorediv">
                <div id="firstteam" >
                    <h2 className="streamingheading">{location.state.info.attackersTeam.name}</h2>

                    <h3 className="streamingscore">{attackersPoints}</h3>

                </div>
                <div id="middleteamzone" >
                    <img id="cdlogo" src={cdlogo}></img>
                </div>
                <div id="secondteam">
                    <h2 className="streamingheading">{location.state.info.defendersTeam.name}</h2>
                    <h3 className="streamingscore">{defendersPoints}</h3>
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