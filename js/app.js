import React from 'react';

const ReactDOM = require('react-dom');

class App extends React.Component {
    state = {
        isLoading: true,
        users: [],
        error: null
    };

    getFetchUsers() {
        this.setState({
            loading: true
        }, () => {
            fetch("/api/staff/").then(res => res.json()).then(result => this.setState({
                loading: false,
                users: result
            })).catch(console.log);
        });
    }

    componentDidMount() {
        this.getFetchUsers();
    }

    render() {
        const {
            users,
            error
        } = this.state;
        return (
            <React.Fragment>
                {error ? <p>
                    {
                        error.message
                    } < /p> : null} {
                users.map(user => {
                    const {
                        name,
                        avatarUrl,
                        description,
                        rank
                    } = user;
                    return (
                        <div className="col-xl-3 col-lg-3 col-md-3 col-sm-12">
                            <div className="testimonial_box">
                                <figure><img className="card-img-top rounded-circle pfp"
                                             src={avatarUrl}
                                             alt={name}/></figure>
                                <h3><b>{name}</b></h3>
                                <h3>{description}</h3>
                                <h3>Rank: {rank}</h3>
                            </div>
                        </div>
                    );
                })
            } < /React.Fragment>);
    }
}

ReactDOM.render(
    <App loggedInManager={document.getElementById('admins').innerHTML}/>,
    document.getElementById('admins')
)