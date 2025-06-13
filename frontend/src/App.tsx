import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

import Layout from './components/Layout';
import BookList from './components/BookList';
import UserList from './components/UserList';
import LoanList from './components/LoanList';
import OverdueList from './components/OverdueList';

function App() {
  return (
    <Router>
      <Layout>
        <Routes>
          <Route path="/" element={<BookList />} />
          <Route path="/users" element={<UserList />} />
          <Route path="/loans" element={<LoanList />} />
          <Route path="/overdue" element={<OverdueList />} />
        </Routes>
      </Layout>
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
      />
    </Router>
  );
}

export default App;
