import '../styles/Event.css';
import React from 'react';
export class EventEntry extends React.Component {
    constructor(props) {
        super(props);
        this.state = {

        };
      this.RetrieveEventDescription=this.RetrieveEventDescription.bind(this);
    }
    RetrieveEventDescription(){
       let description="";
       if(this.props.record.user!==null ||  this.props.record.user!==undefined)
       {
        if(this.props.record.type.startsWith("ATTACKER")) description="The attacker "
        else if(this.props.record.type.startsWith("DEFENDER"))description ="The defender "
        description = description +  this.props.record.user + " " ;
       }
     switch(this.props.record.type){
        case "ATTACKER_JOINED" : 
        description= description + "joined as attacker";
        break;
        case "ATTACKER_MUTANT_CREATED" : 
        description= description + "created a mutant";
        break;
        case "ATTACKER_MUTANT_ERROR" : 
        description= description + "created a mutant that errored";
        break;
        case "ATTACKER_MUTANT_KILLED_EQUIVALENT" : 
        description= description + "created a mutant that errored";
        break;
        case "ATTACKER_MUTANT_SURVIVED" : 
        description= description + "created a mutant that survived";
        break;
        case "DEFENDER_JOINED" : 
        description= description + "joined ad defender";
        break;
        case "DEFENDER_KILLED_MUTANT" : 
        description= description + "killed a mutant";
        break;
        case "DEFENDER_MUTANT_CLAIMED_EQUIVALENT" : 
        description= description + "claimed a mutant equivalent";
        break;
        case "DEFENDER_MUTANT_EQUIVALENT" : 
        description= description + "caught an equivalence";
        break;
        case "DEFENDER_TEST_CREATED" : 
        description= description + "created a test";
        break;
        case "DEFENDER_TEST_ERROR" : 
        description= description + "created a test that errored";
        break;
        case "DEFENDER_TEST_READY" : 
        description= "Test by " + description + "is ready";
        break;
        case "GAME_CREATED" : 
        description= "Game created";
        break;
        case "GAME_FINISHED" : 
        description= "The game is over";
        break;
        case "GAME_GRACE_ONE" : 
        description= "Phase one ended. Phase two started";
        break;
        case "GAME_GRACE_TWO" : 
        description= "Phase two ended. Phase three started";
        break;
        case "GAME_STARTED" : 
        description= "The game has started";
        break;
        case "SCORE_UPDATE" : 
        description= "New Scores";
        break;
     }
     let date = new Date(this.props.record.timestamp);
     let hours = date.getHours();
     let minutes = date.getMinutes();
     let seconds =  date.getSeconds();
     let pmam=""
     if(hours>=12){
        pmam = "p.m."
     }
     else{
        pmam="a.m."
     }
     if(hours>=13){
        hours=hours-12;
     }
     
     if(minutes<10){
        minutes= minutes.toString();
        minutes="0"+ minutes;
     }
     if(seconds<10){
        seconds= seconds.toString();
        seconds="0"+ seconds;
     }
     description = description + " at  " + hours + ":" + minutes + ":" + seconds + " " + pmam;
     return description;
    }
    render() {
        return (
            <> <div id="evententrydiv">
                <div id="typediv">{this.RetrieveEventDescription()}</div>
                
                </div>
            </>
            
        
        )
    }
}
