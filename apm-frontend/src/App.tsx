import { BrowserRouter, Routes, Route } from 'react-router-dom';
import { MainLayout } from '@/layouts/MainLayout';
import { Dashboard } from '@/pages/Dashboard';
import { SystemsPage } from '@/pages/SystemsPage';
import { SystemDetailPage } from '@/pages/SystemDetailPage';
import { AlertsPage } from '@/pages/AlertsPage';

function App() {
  return (
    <BrowserRouter>
      <MainLayout>
        <Routes>
          <Route path="/" element={<Dashboard />} />
          <Route path="/systems" element={<SystemsPage />} />
          <Route path="/systems/:systemId" element={<SystemDetailPage />} />
          <Route path="/alerts" element={<AlertsPage />} />
        </Routes>
      </MainLayout>
    </BrowserRouter>
  );
}

export default App;
