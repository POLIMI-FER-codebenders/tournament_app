import "../styles/MatchEntry.css";
import React from 'react';
import { getData } from "../utils";
import { GoToErrorPage } from "../utils";
import { CDFrame } from "./CDFrame";
import { useNavigate } from "react-router-dom";
import { useEffect } from 'react'
import { GoToPlayPage } from "../utils";
import { GoToStreamingPage } from "../utils";
export class MatchEntry extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            badResponse:null,
            frameactive:false,
            streamactive:false
        };
        this.showCDFrame =this.showCDFrame.bind(this);
        this.showStreaming= this.showStreaming.bind(this);
        
    }
    

    showCDFrame(){
       this.setState({frameactive:true});
    }

    showStreaming(){
        this.setState({streamactive:true});
    }
    
    componentDidMount(){
        if(sessionStorage.getItem("username")!=null){
        getData("/api/match/current_match").then((response) => {
            if (response.status === 200) {
                response.result.id=response.result.id*1 // convert from string to integer
              this.setState({currentMatch: response.result,badResponse:null});

              
            }

             else this.setState({ badResponse: response.message });
            }
            );
          }
    }
    
    render() { 

       

        if(this.state.frameactive) return  <GoToPlayPage info={this.state.currentMatch} />;
        if(this.state.streamactive) return <GoToStreamingPage info={this.props.record}/>
        if (this.state.badResponse!=null) return (<GoToErrorPage path="/error" message={this.state.badResponse} />)

        

        let playbutton;
        if(this.state.currentMatch!=null && this.state.currentMatch.result==="ongoing match found" && this.props.record.id===this.state.currentMatch.id ){
           
            playbutton=<div class="col9-matches flex-items-matches">
        <div class="btn-matches" onClick={this.showCDFrame} >Play</div>
          </div>
        }
    else playbutton=null;
        return (
            <div>
                <div class="list-entry-matches flex-container-matches" >
                    <div class="col1-matches flex-items-matches">{this.props.record.attackersTeam.name}</div>
                    <div class="col2-matches flex-items-matches">{this.props.record.defendersTeam.name}</div>
                    <div class="col3-matches flex-items-matches">{this.props.record.roundNumber}</div>
                    <div class="col4-matches flex-items-matches">{this.props.record.scoreteam1}</div>
                    <div class="col5-matches flex-items-matches">{this.props.record.scoreteam2}</div>
                    <div class="col6-matches flex-items-matches">{this.props.record.status}</div>
                    <div class="col7-matches flex-items-matches"></div>
                    <div class="col8-matches flex-items-matches">
                        <div class="btn-matches" onClick={this.showStreaming} >Live Score</div>
                    </div>
                    {playbutton}
                    
                </div>
            </div>
        );
    }

    
}