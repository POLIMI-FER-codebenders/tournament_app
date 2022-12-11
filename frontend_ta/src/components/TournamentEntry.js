import "../styles/TournamentListComponent.css";
import "../styles/MatchEntry.css";
import React from 'react';
import { MatchEntry } from "./MatchEntry";
import postData, { getData } from "../utils.js";
import { GoToErrorPage } from "../utils.js";

export class TournamentEntry extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            tourcontent: null,
            joinreply: "",
            badResponse: null,
            playerteam: null,
            playertournaments: null,
            selectedround: null,
            selectedclass: null
        };
        this.DisplayTeamForm = this.DisplayTeamForm.bind(this);
        this.JoinButton = this.JoinButton.bind(this);
        this.DisplayTournamentInfo = this.DisplayTournamentInfo.bind(this);
        this.JoinTournament = this.JoinTournament.bind(this);
        this.DisplayClassUploading = this.DisplayClassUploading.bind(this);
        this.SelectClass = this.SelectClass.bind(this);

    }
    componentDidMount() {
        if (sessionStorage.getItem("username") != null) {
             
            getData("/api/team/get-mine").then((response) => {
                if (response.status === 200) {
                    this.setState({ playerteam: response.result });
                    if (this.state.playerteam != null) {
                        getData("/api/tournament/personal").then((response) => {
                            if (response.status === 200) {
                                this.setState({ playertournaments: response.result });

                            }
                            else console.log("error");
                        })
                    }
                }
                else console.log("error");
            })

        }
        else {
            this.setState({ playerteam: null });
            this.setState({ playertournaments: null });

        }
    }
    render() {
        let formtoshow;
        if (this.props.viewindex !== this.props.currentview) formtoshow = null;
        else formtoshow = this.state.tourcontent;
        let joinreplytext;
        if (this.state.joinreply === "OK") joinreplytext = <p>You have successfully joined the tournament!</p>
        else if (this.state.joinreply === "KO") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
        return (
            <div>
                <div className="list-entry flex-container" >
                    <div className="list-entry-details flex-container-details" onClick={this.DisplayTournamentInfo}>
                        <div class="col1 flex-items">{this.props.record.name}</div>
                        <div class="col2 flex-items">27011/2022</div>
                        <div class="col3 flex-items">{this.props.record.type}</div>
                        <div class="col4 flex-items">{this.props.record.status}</div>
                        <div class="col5 flex-items">{this.props.record.teamSize}</div>
                    </div>
                    <div class="col6 flex-items">
                        {this.JoinButton(this.props.record.status)}
                    </div>
                </div>
                {formtoshow}
                {joinreplytext}
            </div>
        );
    }
    SelectClass(event) {
        event.preventDefault();
        
        let roundnumber = document.getElementById("roundid").value;

        let classid = document.getElementById("classes").value;
        let data = { idTournament: this.props.record.id, roundNumber: roundnumber, classId: classid };
        if(roundnumber>this.props.record.numberOfRounds){
          this.setState({tourcontent: <p>please insert a valid round number from 1 to {this.props.record.numberOfRounds}</p>});
          return;
        }
        postData("/api/classes/post-choices", data).then((response) => {
            if (response.status === 200) {
                this.setState({ tourcontent: <p>class successfully selected </p> })
                this.props.reloadPage();
            }
            else {
                this.setState({ joinreply: "KO" });
                this.setState({ badResponse: response.message });
            }
        }
        );
    }

    DisplayClassUploading() {
        
       
        if (this.props.record.creator.name === sessionStorage.getItem("username")) {
            let formtoreturn;
            console.log(this.props.classes);
           let maxround=this.props.record.numberOfRounds.toString();
            formtoreturn =
                <div id="displayclassupload">
                    <form onSubmit={this.SelectClass}>
                        <label className="labelclassupload" htmlFor="roundid" >Select the round to which apply a class</label>
                        <input type="number" id="roundid" required min="1" max={maxround}  ></input>
                        <label className="labelclassupload" htmlFor="classes">Select a class for the selected round:</label>
                        <select id="classes" name="classes" required>
                            {this.props.classes.map((elem, i) => <option value={elem.id} key={i}>{elem.filename}</option>)}
                        </select>
                        <div className="button-class-container">
                            <button id="classbuttonupload" type="submit">Select Class </button>
                        </div>
                    </form>
                    
                </div>

            return formtoreturn;
        }
        else return null;
    }

    JoinButton(status) {
        let data = this.state.playerteam
        let teamsintournament = this.props.record.tournamentScores.map(record => record.team.id);

        if (data != null && teamsintournament.includes(data.id)) {
            return <div className="joined" >Joined</div>
        }
        if (status === "TEAMS_JOINING") {
            return <div className="joinable" onClick={this.DisplayTeamForm}>Join</div>
        }
        else if (status === "SCHEDULING" || status === "IN PROGRESS" || status === "ENDED") {
            return <div className="btn" >Join</div>
        }
    }
    DisplayTournamentInfo() {
        let classuploading = this.DisplayClassUploading();
        let content;
        let winner;
        let teamstext = "the current joined teams are ";
        let teamsnames = this.props.record.tournamentScores.map(elem => elem.team.name);
        if (teamsnames.length > 0) {
            teamsnames.forEach((teamtext) => teamstext += teamtext + ",");
            teamstext = teamstext.replace(/.$/, '.');
        }
        else teamstext = "no team have joined yet"
        let summarystatusteam= "The tournament is a " + this.props.record.numberOfTeams.toString() + " teams tournament,there are still " + (this.props.record.numberOfTeams - teamsnames.length).toString() + " slots "
        let teams = <p>{teamstext}<br/> {summarystatusteam}</p>
        if (this.props.record.status !== "TEAMS_JOINING") {
            //  if (this.props.record.status == "ended") winner = <p> The tournament is ended, the winner is {tourinfo.winner}</p>
            //    else winner = null;

            content = (<div>

                {teams}
                <p>Here is the list of scheduled matches</p>
                <div class="list-matches">
                    <div class="list-headers-matches flex-container">
                        <div class="col1-matches flex-items-matches">Team1</div>
                        <div class="col2-matches flex-items-matches">Team2</div>
                        <div class="col3-matches flex-items-matches">Round</div>
                        <div class="col4-matches flex-items-matches">Team1 Score</div>
                        <div class="col5-matches flex-items-matches">Team2 Score</div>
                        <div class="col6-matches flex-items-matches">Status</div>
                        <div class="col7-matches flex-items-matches">Winner</div>
                    </div>
                </div>
                {this.props.record.matches.map((object, i) => <MatchEntry record={object} key={object.id} viewindex={i} refreshView={this.refreshView} currentview={this.state.currentview} player={this.state.playerteam} />)}
            </div>
            );
        }
        else { //the game is not started yet
            content =
                <div id="classuploadinfo">
                    {classuploading}
                    {teams}
                    
                </div>
        }
        this.props.refreshView(this.props.viewindex);
        this.setState({ tourcontent: content })
    }
    JoinTournament(event) {
        event.preventDefault();
        let data = { idTournament: this.props.record.id };
        postData("/api/tournament/join", data).then((response) => {
            if (response.status === 200) {
                this.setState({ tourcontent: <p> successfully joined </p> })
                this.props.reloadPage();
            }
            else {
                this.setState({ joinreply: "KO" });
                this.setState({ badResponse: response.message });
            }
        }
        );
    }
    DisplayTeamForm() {
        if (sessionStorage.getItem("username") === null) {
            this.props.backHome(this.props.index);
            return;
        };
        let formtodisplay;
        if(this.state.playerteam==null) formtodisplay = <p>you are not inside any team, join a team to play a tournament</p>
        else if(!(this.state.playerteam.teamMembers.find(elem=>elem.username=== sessionStorage.getItem("username")
          && elem.role==="LEADER"))){
            formtodisplay = <p>you are not the leader of your team. Please ask the leader to join this tournament</p>
        }
        else if (this.state.playertournaments !== null && this.state.playertournaments.filter(elem => elem.status !== "ENDED").length > 0) {
            formtodisplay = <p>your team is already in a tournament.You can join only a tournament at a time</p>
        }
        else if (this.state.playerteam !== null && this.state.playerteam.teamMembers.length !== this.props.record.teamSize) {
            formtodisplay = <p>your team must be of {this.props.record.teamSize} members to join this tournament</p>
        }

        else if (this.state.playerteam !== null) {
            formtodisplay = (
                <div>
                    <form onSubmit={this.JoinTournament}>
                        <label htmlFor="teamtojointour">Join with your team: {this.state.playerteam.name}</label>
                        <input type="submit" value="Join Tournament" />
                    </form>

                </div>
            )
        }
        else {
            formtodisplay = (
                <div>
                    <p>To join a tournament you must be owner of one team</p>
                </div>
            )
        }
        this.props.refreshView(this.props.viewindex);
        this.setState({ tourcontent: formtodisplay });

    }



}

