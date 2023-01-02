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
            selectedround: null,
            selectedclass: null,
            entryid: "entry" + this.props.viewindex,
            classes: []
            
        };
        this.DisplayTeamForm = this.DisplayTeamForm.bind(this);
        this.JoinButton = this.JoinButton.bind(this);
        this.DisplayTournamentInfo = this.DisplayTournamentInfo.bind(this);
        this.JoinTournament = this.JoinTournament.bind(this);
        this.DisplayClassUploading = this.DisplayClassUploading.bind(this);
        this.SelectClass = this.SelectClass.bind(this);
        this.ChangeColorSelection = this.ChangeColorSelection.bind(this);
        this.ScoreboardButton = this.ScoreboardButton.bind(this);
        this.ShowScoreboard = this.ShowScoreboard.bind(this);
        this.RefreshClasses=this.RefreshClasses.bind(this);
        this.DisplayClassChoices=this.DisplayClassChoices.bind(this);


    }
    componentDidMount() {
        
        this.RefreshClasses();
    }
    RefreshClasses(){
        if (sessionStorage.getItem("username") != null && this.props.record.creator.name === sessionStorage.getItem("username")) {
            getData("/api/classes/get-choices?tournamentId=" + this.props.record.id).then((response) => {
                if (response.status === 200) {
                    
                    this.setState({ classes: response.result });
                   
                }
                else this.setState({ badResponse: response.message });
            }
            );

        }
    }

    render() {

        let formtoshow;
        if (this.props.viewindex !== this.props.currentview) formtoshow = null;
        else formtoshow = this.state.tourcontent;
        let joinreplytext;
        let datestring = "Still to be defined";
        if (this.props.record.startDate != null) {
            let date = new Date(this.props.record.startDate);
            datestring = date.toLocaleDateString() + " " + date.toLocaleTimeString();
        }

        if (this.state.joinreply === "OK") joinreplytext = <p>You have successfully joined the tournament!</p>
        else if (this.state.joinreply === "KO") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
        return (
            <div>
                <div className="list-tour-entry " id={this.state.entryid} >
                    <div className="flex-container" >
                        <div className="list-entry-details flex-container-details" onClick={this.DisplayTournamentInfo}>
                            <div class="col1 flex-items">{this.props.record.name}</div>
                            <div class="col2 flex-items">{datestring}</div>
                            <div class="col3 flex-items">{this.props.record.type}</div>
                            <div class="col4 flex-items">{this.props.record.status}</div>
                            <div class="col5 flex-items">{this.props.record.teamSize}</div>
                        </div>

                        <div class="col6 flex-items">
                            <div className="tournamentbuttons">
                                {this.JoinButton(this.props.record.status)}
                                {this.ScoreboardButton(this.props.record.status)}
                            </div>
                        </div>
                    </div>
                    {formtoshow}
                    {joinreplytext}
                </div>
            </div>
        );
    }
    SelectClass(event) {
        event.preventDefault();

        let roundnumber = document.getElementById("roundid").value;

        let classid = document.getElementById("classes").value;
        let data = { idTournament: this.props.record.id, roundNumber: roundnumber, classId: classid };
        if (roundnumber > this.props.record.numberOfRounds) {
            this.setState({ tourcontent: <p>Please insert a valid round number from 1 to {this.props.record.numberOfRounds}.</p> });
            return;
        }
        postData("/api/classes/post-choices", data).then((response) => {
            if (response.status === 200) {
                this.RefreshClasses();
                this.setState({
                    tourcontent:
                        <div className="confirmdiv">
                            <p>Class successfully selected.</p>
                            <button className="confirmbutton" onClick={() => {
                                this.DisplayTournamentInfo();
                            }
                            } >OK</button>
                        </div>
                })

                
            }
            else {
                this.setState({ joinreply: "KO" });
                this.setState({ badResponse: response.message });
            }
        }
        );
    }
    DisplayClassChoices(){
        let formtoreturn;
        if (this.props.record.creator.name === sessionStorage.getItem("username")) {
           
            formtoreturn =
        <div id="classescontainer"> 
        <p id="classesselectedp" className="name">Classes selected so far:</p>
        <table id="classesselection">
                        <thead>
                            <tr className="classestr">
                                <th className="classesth">Round</th>
                                <th className="classesth">Class Name</th>
                                <th className="classesth">Author</th>
                            </tr>
                        </thead>
                        <tbody>
                            {this.state.classes.map((object, i) => <tr key={i}>
                                <td className="classestd">{object.round}</td>
                                {object.gameClass !== null &&
                                    <td className="classestd"> {object.gameClass.filename}</td>
                                }
                                {object.gameClass !== null &&
                                    <td className="classestd"> {object.gameClass.author}</td>
                                }
                                {object.gameClass === null &&
                                    <td className="classestd">NA</td>
                                }
                                {object.gameClass === null &&
                                    <td className="classestd"> NA</td>
                                }
                            </tr>
                            )}
                        </tbody>
                    </table>
                    </div>
        }
        else formtoreturn=null;
        return formtoreturn;
    }
    DisplayClassUploading() {


        if (this.props.record.creator.name === sessionStorage.getItem("username")) {
            let formtoreturn;

            let maxround = this.props.record.numberOfRounds.toString();
            formtoreturn =
                <>
                    
                    <div className="class">
                        <span className="name">Choice of class for each round:</span>
                        <form className="displayclassupload" onSubmit={this.SelectClass}>
                            <div className="input-bloc">

                                <div className="input-container">
                                    <label htmlFor="roundid" >Round:</label>
                                    <input type="number" id="roundid" required min="1" max={maxround}  ></input>
                                </div>
                                <div className="input-container">
                                    <label htmlFor="classes">Class:</label>
                                    <select className="selector" id="classes" name="classes" required>
                                        {this.props.classes.map((elem, i) => <option value={elem.id} key={i}>{elem.filename}</option>)}
                                    </select>
                                </div>
                            </div>
                            <div className="button-container classbuttonupload">
                                <input type="submit" value="Select Class" />
                            </div>
                        </form>

                    </div>
                </>

            return formtoreturn;
        }
        else return null;
    }

    DisplayTeamInfo() {
        let teamstext = "";
        let teamsnames = this.props.record.tournamentScores.map(elem => elem.team.name);
        if (teamsnames.length > 0) {
            for (let index = 0; index < teamsnames.length; index++) {
                const teamtext = teamsnames[index];
                teamstext += teamtext
                if (index === teamsnames.length - 2) {
                    teamstext += " and ";
                }
                else if (index < teamsnames.length - 2) {
                    teamstext += ", ";
                }
            }
        }
        else teamstext = "no team has joined yet"
        return (
            <div>
                <div className={"flex-container-team-col"}>
                    <span className="flex-items-team name">Teams participating in the tournament:</span>
                    <span className="flex-items-team">{teamstext}</span>
                </div>

                {!(this.props.record.status==="ENDED" || this.props.record.status==="IN_PROGRESS")   && <>
                <div className="flex-container-team-row">
                    <span className="flex-items-team name">Number of teams required:</span>
                    <span className="flex-items-team">{this.props.record.numberOfTeams.toString()}</span>
                </div>
                <div className="flex-container-team-row">
                    <span className="flex-items-team name">Remaining number of team slots:</span>
                    <span className="flex-items-team">{(this.props.record.numberOfTeams - teamsnames.length).toString()}</span>
                </div></>}

            </div>
        )
    }
    ShowScoreboard() {
        this.ChangeColorSelection();
        console.log(this.props.record);
        let type;
        let score;
        if (this.props.record.type === "KNOCKOUT") {

            type = "Number of wins";
        }

        else type = "League points";

        let content = <div id="tablecontainer"> <table id="scoreboard">
            <thead>
                <tr className="scoreboardtr">
                    <th className="scoreboardth">Team Name</th>
                    <th className="scoreboardth">{type}</th>
                </tr>
            </thead>
            <tbody>
                {this.props.record.tournamentScores.map((object, i) => <tr key={i}>
                    <td className="scoreboardtd">{object.team.name}</td>
                    {this.props.record.type == "KNOCKOUT" &&
                        <td className="scoreboardtd"> {object.leaguePoints}</td>
                    }
                    {this.props.record.type === "LEAGUE" &&
                        <td className="scoreboardtd"> {object.leaguePoints}</td>}
                </tr>
                )}
            </tbody>
        </table>
        </div>
        this.props.refreshView(this.props.viewindex);
        this.setState({ tourcontent: content })

    }
    JoinButton(status) {

        let data = this.props.playerteam
        let teamsintournament = this.props.record.tournamentScores.map(record => record.team.id);

        if (data != null && teamsintournament.includes(data.id)) {
            return <div className="joined" >Joined</div>
        }
        if (status === "TEAMS_JOINING") {
            return <div className="joinable" onClick={this.DisplayTeamForm}>Join</div>
        }
        else if (status === "SCHEDULING" || status === "SELECTING_CLASSES") {
            return <div className="btn" >Join</div>
        }
    }
    ScoreboardButton(status) {
        if (status === "IN_PROGRESS" || status === "ENDED") {
            return <div className="scoreboardbutton" onClick={this.ShowScoreboard}>Scoreboard</div>
        }
    }

    ChangeColorSelection() {
        let currententry = document.getElementById(this.state.entryid);
        let tourentries = document.getElementsByClassName("list-tour-entry");
        for (let i = 0; i < tourentries.length; i++) { tourentries[i].style.backgroundColor = "transparent" }
        currententry.style.backgroundColor = "rgb(246, 246, 246)";
    }
    DisplayTournamentInfo(refresh) {
        this.ChangeColorSelection();

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
        let summarystatusteam = "The tournament is a " + this.props.record.numberOfTeams.toString() + " teams tournament,there are still " + (this.props.record.numberOfTeams - teamsnames.length).toString() + " slots "
        // let teams = <p>{teamstext}<br/> {summarystatusteam}</p>
        // let teams = this.DisplayTeamInfo();
        if (this.props.record.status !== "TEAMS_JOINING" && this.props.record.status !== "SELECTING_CLASSES") {


            if (this.props.record.status == "ENDED") winner = <p id="winnertournament"> Winner:  <span id="winnerspan">{this.props.record.winningTeam.name}</span></p>
            else winner = null;


            content = (<div>

                {this.DisplayTeamInfo()}
                {winner}
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
                {this.props.record.matches.map((object, i) => <MatchEntry record={object} key={object.id} viewindex={i} refreshView={this.refreshView} currentview={this.state.currentview} player={this.props.playerteam} />)}
            </div>
            );
        }
        else { //the game is not started yet
            content =
                ( <>
                {this.DisplayClassChoices()}
                <div id="classuploadinfo">
                    {this.DisplayTeamInfo()}
                    {classuploading}
                </div>
                </>
                )
        }
        this.props.refreshView(this.props.viewindex);
        this.setState({ tourcontent: content })
    }
    JoinTournament(event) {
        event.preventDefault();
        let data = { idTournament: this.props.record.id };
        postData("/api/tournament/join", data).then((response) => {
            if (response.status === 200) {
                this.setState({
                    tourcontent: <div className="confirmdiv">
                        <p>Successfully joined.</p>
                        <button className="confirmbutton" onClick={this.DisplayTournamentInfo}>OK</button>
                    </div>
                })
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
        this.ChangeColorSelection();

        if (sessionStorage.getItem("username") === null) {
            this.props.backHome(this.props.index);
            return;
        };
        let formtodisplay;
        if (this.props.playerteam == null) formtodisplay = <p>you are not inside any team, join a team to play a tournament</p>
        else if (!(this.props.playerteam.teamMembers.find(elem => elem.username === sessionStorage.getItem("username")
            && elem.role === "LEADER"))) {
            formtodisplay = <p>You are not the leader of your team. Please ask the leader to join this tournament.</p>
        }
        else if (this.props.playertournaments !== null && this.props.playertournaments.filter(elem => elem.status !== "ENDED").length > 0) {
            formtodisplay = <p>your team is already in a tournament.You can join only a tournament at a time</p>
        }
        else if (this.props.playerteam !== null && this.props.playerteam.teamMembers.length !== this.props.record.teamSize) {
            formtodisplay = <p>your team must be of {this.props.record.teamSize} members to join this tournament</p>
        }

        else if (this.props.playerteam !== null) {


            formtodisplay = (
                <div>
                    <form onSubmit={this.JoinTournament} className="join-bloc">
                        <div className="input-container">
                            <label htmlFor="teamtojointour">Join with your team: {this.props.playerteam.name}</label>
                            <input type="submit" id="teamtojointour" value="Join Tournament" />
                        </div>
                    </form>

                </div>
            )

        }
        else {
            formtodisplay = (
                <div>
                    <p>To join a tournament, you must be owner of one team.</p>
                </div>
            )
        }
        this.props.refreshView(this.props.viewindex);
        this.setState({ tourcontent: formtodisplay });

    }



}
