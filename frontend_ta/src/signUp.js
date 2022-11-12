import React from 'react';
import SignIn from './signIn';
import postData from './utils';
class SignUp extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
          errorMessage: null,
          isSubmitted:false,
          username :'',
          password :'',
          confirmpassword :'',
          email:'',
          view :"SignUp"
        };
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
      }
   
 renderErrorMessage(){
    if(this.state.errorMessage!=null)return;
  return(
    <div className="error">{this.state.errorMessage}</div>
  );
 }

 handleChange(event) {
  if(event.target.name=="username")this.setState({username: event.target.value});
  else if (event.target.name=="password") this.setState({password: event.target.value});
  else if (event.target.name=="email") this.setState({email: event.target.value});
  else if (event.target.name=="confirmpassword") this.setState({confirmpassword: event.target.value});

 }

  handleSubmit(event) {
    event.preventDefault();
    let data={username: this.state.username,password:this.state.password,email:this.state.email,confirmpassword:this.state.confirmpassword};
    console.log(data);
    let url=""
    /*
  postData(url, data)
  .then((response) => {
    console.log(response); //  
  });
  */
  }

    
   render() {
    if (this.state.view=="SignIn") return (<SignIn/>);
    else if(this.state.view=="SignUp") return (
<div className="app">
    <div className="login-form">
          <div className="title">Sign Up</div>
    <div className="form">
      <form onSubmit={this.handleSubmit}>
        <div className="input-container">
          <label>Username </label>
          <input type="text" name="username" value={this.state.username} onChange={this.handleChange} required />
        </div>
        <div className="input-container">
          <label>Email </label>
          <input type="text" name="email" value={this.state.email} onChange={this.handleChange} required />
        </div>
        <div className="input-container">
          <label>Password </label>
          <input type="password" name="password" value={this.state.password} onChange={this.handleChange} required />
        </div>
         <div className="input-container">
          <label>Confirm Password </label>
          <input type="password" name="confirmpassword" value={this.state.confirmpassword} onChange={this.handleChange} required />
         </div>
        <div className="button-container">
          <input type="submit" value="Sign up"/>
        </div>
      </form>
      {this.renderErrorMessage()}
      <button className="formbutton" onClick={()=>this.setState({view:"SignIn"})}> Sign in if you already have an account </button>
    </div>
  </div>
</div>
 );
    }
  }
export default SignUp;