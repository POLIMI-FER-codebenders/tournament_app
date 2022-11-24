import "../styles/TournamentListComponent.css";
import React from 'react';

export class TournamentEntry extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            form: null
        };
        this.DisplayTeamForm = this.DisplayTeamForm.bind(this)
        this.JoinButton = this.JoinButton.bind(this);
        this.DisplayTournamentInfo=this.DisplayTournamentInfo.bind(this);
    }
    render() {
        let formtoshow;
        if (this.props.viewindex != this.props.currentview) formtoshow = null;
        else formtoshow = this.state.form;
        return (
            <div>
                <div class="list-entry flex-container" onClick={this.DisplayTournamentInfo}>
                    <div class="col1 flex-items">{this.props.record.name}</div>
                    <div class="col2 flex-items">{this.props.record.date}</div>
                    <div class="col2 flex-items">{this.props.record.status}</div>
                    <div class="col3 flex-items">
                        {this.JoinButton(this.props.record.status)}
                    </div>
                    <div class="col4 flex-items">
                        <div class="btn" >Live Score</div>
                    </div>
                </div>
                {formtoshow}
            </div>
        );
    }

    JoinButton(status) {
        if (status == "started") {
            return <div className="joinable" onClick={this.DisplayTeamForm} >Join</div>
        }
        else if (status == "not started") {
            return <div className="btn" >Join</div>
        }
    }
    DisplayTournamentInfo(){

            let content;
            content=<p>Content</p>
            this.props.refreshView(this.props.viewindex);
            this.setState({ form: content })
    }
    DisplayTeamForm() {

        let data = 
            { "name": "Steaming hot coffee enjoyers" };
        let formtodisplay;
        if(data!=null){
         formtodisplay = (
            <div>
                <form>
                    <label forhtml="teamtojointour">Join with your team: {data.name}</label>
                </form>
                <input type="submit" value="Join Tournament" />
            </div>
        )
        }
        else{
            formtodisplay = (
                <div>
                    <p>To join a tournament you must be owner of one team</p>
                </div>
            )
        }
        this.props.refreshView(this.props.viewindex);
        this.setState({ form: formtodisplay })

    }
}
