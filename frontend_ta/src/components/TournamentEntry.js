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
    }
    render() {
        let formtoshow;
        if (this.props.viewindex != this.props.currentview) formtoshow = null;
        else formtoshow = this.state.form;
        return (
            <div>
                <div class="list-entry flex-container">
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
    DisplayTeamForm() {

        let data = [
            { "name": "Steaming hot coffee enjoyers" },
            { "name": "Guys from the basement" }
        ];
        let formtodisplay = (
            <div>
                <form>
                    <label forhtml="teamtojointour">Choose a team:</label>
                    <select name="team" id="teamtojointour">
                        {data.map((object, i) => <option record={object} key={i} value={object.name}>{object.name}</option>)}
                    </select>
                </form>
                <input type="submit" value="Join Tournament" />
            </div>
        )
        this.props.refreshView(this.props.viewindex);
        this.setState({ form: formtodisplay })

    }
}
