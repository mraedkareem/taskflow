import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

// Wraps a page that requires login: no token → redirect to /login.
export default function ProtectedRoute({ children }) {
  const { token } = useAuth();
  return token ? children : <Navigate to="/login" replace />;
}
