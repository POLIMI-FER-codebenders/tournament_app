import React from 'react';
import postData from '../utils';
import SignUp from './signUp';
import "../styles/SignInUp.css";
import '../styles/App.css';

class SignIn extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
          errorMessage: null,
          isSubmitted:false,
          username :'',
          password :'',
          view :"SignIn"
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
      }
   
 renderErrorMessage(){
    if(this.state.errorMessage==null)return;
  return(
    <p>{this.state.errorMessage}</p>
  );
 }

 handleChange(event) {
  if(event.target.name=="username")this.setState({username: event.target.value});
  else if (event.target.name=="password") this.setState({password: event.target.value});
 }

  handleSubmit(event) {
    event.preventDefault();
    let data={username: this.state.username,password:this.state.password};
    console.log(data);
    let url="http://localhost:8080/authentication/login"
    
  postData(url, data)
  .then((response) => {
    if(response.result){
      
      this.setState({errorMessage: "logged in"})
    }
    else{
      this.setState({errorMessage:"invalid credentials"})
    }
  });
  
  }

    
   render() {
    let nextcomponent;
    console.log( "la view è"  + this.state.view);
    if (this.state.view=="SignUp") nextcomponent= (<SignUp/>);
    if(this.state.view=="SignIn") nextcomponent= (
      <div className="app" class="main-panel">
        <div className="login-form">
          <div className="title">Sign In</div>
    <div className="form">
      <form onSubmit={this.handleSubmit}>
        <div className="input-container">
          <label>Username </label>
          <input type="text" name="username" value={this.state.username} onChange={this.handleChange} required />
        </div>
        <div className="input-container">
          <label>Password </label>
          <input type="password" name="password" value={this.state.password} onChange={this.handleChange} required />
        </div>
        <div className="button-container">
          <input type="submit" value="Sign in"/>
        </div>
      </form>
      {this.renderErrorMessage()}
      <button className="formbutton" onClick={()=>this.setState({view:"SignUp"})}> Create Account </button>
      
    </div>
  </div>
</div>

    
 );
     return nextcomponent;
    }
    
  }
  
export default SignIn;
