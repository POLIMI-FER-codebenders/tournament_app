
import "../styles/iframe.css"
import React from 'react';
import { getData } from "../utils";
import { GoToErrorPage } from "../utils";
import { useLocation } from 'react-router-dom';
import clock from '../images/clock.png';
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { getActiveElement } from "@testing-library/user-event/dist/utils";
export function CDFrame(props) {

  const [days, setDays] = useState(0);
  const [hours, setHours] = useState(0);
  const [minutes, setMinutes] = useState(0);
  const [seconds, setSeconds] = useState(0);
  const [cphase,setCphase] = useState(0);
  const[datarender,setDatarender]=useState(0);
  let data;
  let beginphase;
  let endphase;
  let timeforphase;
  let intervalstopper;
  let currentphase=0;
  let now;
  const location = useLocation();
  let navigate = useNavigate();

  
  function RetrievePhaseDuration(){
    if(currentphase==1)return data.phaseOneDuration;
    if(currentphase==2)return data.phaseTwoDuration;
    if(currentphase==3)return data.phaseThreeDuration;
  }
  function ComputeBeginTime(){
    if(currentphase==1) return data.startDate;
    if(currentphase==2)return data.startDate + data.phaseOneDuration;
    if(currentphase=3)return data.startDate + data.phaseOneDuration+ data.phaseTwoDuration;
  }
  function ComputeTimerPhaseInfo(){

    let startstring=new Date(data.startDate);
    console.log(startstring);
    beginphase =  ComputeBeginTime(data);
    let beginphasestring= new Date(beginphase);
    console.log(beginphasestring);
    endphase = beginphase + RetrievePhaseDuration();
   
    timeforphase = endphase - now;
    let endphasestring=  new Date(timeforphase);
    console.log(endphasestring)
    let timeforphasestring = new Date(timeforphase);
    console.log(timeforphasestring);


    if(timeforphase<0)timeforphase=0;
  }
  function UpdateTimer(deadline) {
   
    timeforphase = timeforphase - 1000;
    if (timeforphase <= 0){
       clearInterval(intervalstopper);
       if(currentphase<3){
        currentphase++;
        setCphase(currentphase);
        console.log(data);
        ComputeTimerPhaseInfo(data);
        intervalstopper = setInterval(() => UpdateTimer(), 1000);
       }
    }
    setDays(Math.floor(timeforphase / (1000 * 60 * 60 * 24)));
    setHours(Math.floor((timeforphase / (1000 * 60 * 60)) % 24));
    setMinutes(Math.floor((timeforphase / 1000 / 60) % 60));
    setSeconds(Math.floor((timeforphase / 1000) % 60));

  }
  function InitializeTimer(){
    now = Date.now();
    let nowstring =new Date(now);
    //console.log(nowstring);
    let startdatestring= new Date(data.startDate);
    //console.log(startdatestring);
    let gamestart= data.startDate;
    let difference = new Date(nowstring.getTime() - startdatestring.getTime());

    let start=new Date(1000);
    //console.log(difference);
    //console.log(start);
  //console.log( Date(_data.phaseOneDuration));
  //  console.log(_data.phaseOneDuration)
    
    if(now - gamestart< data.phaseOneDuration) currentphase=1;
    else if(now - gamestart<data.phaseOneDuration  + data.phaseTwoDuration ) currentphase=2;
    else if(now - gamestart<data.phaseOneDuration + data.phaseTwoDuration  + data.phaseThreeDuration  ) currentphase=3;
    else currentphase=4;
    setCphase(currentphase);
    ComputeTimerPhaseInfo();
    intervalstopper = setInterval(() => UpdateTimer(), 1000);
  }
  useEffect(() => {

    let page = document.getElementsByTagName("html")[0];
    page.style.overflowX="hidden";
    page.style.overflowY="hidden";
    console.log(location.state)
    if (location.state == null) {
      if (sessionStorage.getItem("username") != null) {
        getData("/api/match/current_match").then((response) => {
          if (response.status === 200) {
            response.result.startDate=response.result.startDate*1 // convert startDate from string to integer
            response.result.phaseOneDuration=response.result.phaseOneDuration*1000;
            response.result.phaseTwoDuration=response.result.phaseTwoDuration*1000;
            response.result.phaseThreeDuration=response.result.phaseThreeDuration*1000;
            data=response.result;
            setDatarender(data);
            InitializeTimer(response.result);
          }
          else {
            navigate("/error", { state: { message: response.message } })
          }
        }
      
        );
      }

    }
    else {
      location.state.info.startDate=location.state.info.startDate*1; // convert startDate from string to integer
      location.state.info.phaseOneDuration= location.state.info.phaseOneDuration*1000;
      location.state.info.phaseTwoDuration= location.state.info.phaseTwoDuration*1000;
      location.state.info.phaseThreeDuration= location.state.info.phaseThreeDuration*1000;
      data=location.state.info;
      setDatarender(data);
      InitializeTimer(location.state.info);
          }

          
      return function cleanup(){
        let page = document.getElementsByTagName("html")[0];
        page.style.overflowX="visible";
        page.style.overflowY="visible";
      }
  }, [])





  function DisplayContent() {
    let timertext;
    if(cphase==0) timertext="Starting timer"
    else if(cphase==1) timertext = "Phase 1  D: " + days.toString() + " H: " + hours.toString() + " M: " + minutes.toString() + " S: " + seconds.toString();
    else if(cphase==2) timertext = "Phase 2  D: " + days.toString() + " H: " + hours.toString() + " M: " + minutes.toString() + " S: " + seconds.toString();
    else if(cphase==3) timertext = "Phase 3  D: " + days.toString() + " H: " + hours.toString() + " M: " + minutes.toString() + " S: " + seconds.toString();
    else if(cphase==4) timertext = "Ended"
    if (datarender == null) return (<div>
      <p> you are not inside any game</p>
    </div>);
    else {
      let cdurl = datarender.server + "/login?token=" + datarender.token;
      return (
        <div id="maindiv">

          <div id="framemenu">
            <a href="http://localhost:3000" id="backlink">Back To Tournament Application</a>
            <img id="clock" src={clock}></img>
            <p id="time" >{timertext}</p>

          </div>
          <div id="iframediv">
            <iframe id="iframe" src={cdurl} title="cdtournamentsgames" allowFullScreen  ></iframe>
          </div>
        </div>

      );

    }
  }
  return DisplayContent();

}






