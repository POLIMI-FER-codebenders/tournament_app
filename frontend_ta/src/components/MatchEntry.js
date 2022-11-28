import "../styles/MatchEntry.css";
import React from 'react';

export class MatchEntry extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
        };
    }
    render() { 
        return (
            <div>
                <div class="list-entry-matches flex-container-matches" >
                    <div class="col1-matches flex-items-matches">{this.props.record.team1name}</div>
                    <div class="col2-matches flex-items-matches">{this.props.record.team2name}</div>
                    <div class="col3-matches flex-items-matches">{this.props.record.round}</div>
                    <div class="col4-matches flex-items-matches">{this.props.record.scoreteam1}</div>
                    <div class="col5-matches flex-items-matches">{this.props.record.scoreteam2}</div>
                    <div class="col6-matches flex-items-matches">{this.props.record.status}</div>
                    <div class="col7-matches flex-items-matches">{this.props.record.knockwinner}</div>
                    <div class="col8-matches flex-items-matches">
                        <div class="btn-matches" >Live Score</div>
                    </div>
                    
                </div>
            </div>
        );
    }

    
}