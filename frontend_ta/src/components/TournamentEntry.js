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
            form: null,
            joinreply: "",
            badResponse: null
        };
        this.DisplayTeamForm = this.DisplayTeamForm.bind(this)
        this.JoinButton = this.JoinButton.bind(this);
        this.DisplayTournamentInfo = this.DisplayTournamentInfo.bind(this);
        this.JoinTournament = this.JoinTournament.bind(this);

    }
    render() {
        let formtoshow;
        if (this.props.viewindex != this.props.currentview) formtoshow = null;
        else formtoshow = this.state.form;
        let joinreplytext;
        if (this.state.joinreply == "OK") joinreplytext = <p>You have successfully joined the tournament!</p>
        else if (this.state.joinreply == "KO") return (<GoToErrorPage path="/error" message={this.state.badResponse} />);
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

    JoinButton(status) {
        if (status == "TEAMS_JOINING") {
            return <div className="joinable" onClick={this.DisplayTeamForm}>Join</div>
        }
        else if (status == "SCHEDULING" || status == "IN PROGRESS" || status == "ENDED") {
            return <div className="btn" >Join</div>
        }
    }
    DisplayTournamentInfo() {
        const tourinfo = {
            teams: [
                { name: "team1" },
                { name: "team2" },
            ],
            currentround: 1,
            totalnumberofteams: 2,
            currentnumberofteams: 2,
            winner: "team1"
        }

        const datamatch = [
            { "team1name": "hdhdh", "team2name": "ddd", "round": 1, "status": "ended", "scoreteam1": 34, "scoreteam2": 47, "type": "knockout", "knockround": 1, "knockwinner": "team2", "leagueresult": null },
            { "team1name": "asdsa", "team2name": "dertfdf", "round": 2, "status": "started", "scoreteam1": 34, "scoreteam2": 47, "type": "knockout", "knockround": 1, "knockwinner": null, "leagueresult": null },

        ];
        //   Match(ID,tournamentid,team1id,team2id,scoreteam1,scoreteam2,status,type,knockround*,knockwinnerid*,leagueresult*)
        let content;
        let winner;
        if (this.props.record.status != "not-started") {
            if (this.props.record.status == "ended") winner = <p> The tournament is ended, the winner is {tourinfo.winner}</p>
            else winner = null;
            content = <div>
                {winner}
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
                {datamatch.map((object, i) => <MatchEntry record={object} key={i} viewindex={i} refreshView={this.refreshView} currentview={this.state.currentview} />)}
            </div>
        }
        else { //the game is not started yet 
            content = <p> The total number of teams for this tournament is {tourinfo.totalnumberofteams}, there are still {tourinfo.totalnumberofteams - tourinfo.currentnumberofteams} slots </p>
        }
        this.props.refreshView(this.props.viewindex);
        this.setState({ form: content })
    }
    JoinTournament(event) {
        event.preventDefault();
        let data = { idTournament: this.props.record.id };
        postData("/api/tournament/join", data).then((response) => {
            if (response.status == 200) this.setState({ joinreply: "OK" });
            else {
                this.setState({ joinreply: "KO" });
                this.setState({ badResponse: response.message });
            }
        }
        );
    }
    DisplayTeamForm() {
        if (sessionStorage.getItem("username") == null) {
            console.log(this.props.index);
            this.props.backHome(this.props.index);
            return;
        };

        getData("/api/team/get-mine").then((response) => {
            if (response.status == 200) {
                let data = response.result
                let formtodisplay;
                if (data != null) {
                    formtodisplay = (
                        <div>
                            <form onSubmit={this.JoinTournament}>
                                <label forhtml="teamtojointour">Join with your team: {data.name}</label>
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
                this.setState({ form: formtodisplay })
            }
            else console.log("error");
        }
        )


    }
}
