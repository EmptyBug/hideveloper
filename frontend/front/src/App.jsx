import React from 'react';
import { BrowserRouter, Routes, Route, useNavigate } from 'react-router-dom';

import TalkGenPage from './pages/imagegen/Page';

// 1. 메인 홈 화면 컴포넌트
function Home() {
  const navigate = useNavigate();

  const features = [
    { id: 1, title: '인간 사진 생성', path: '/imageGen-model' },
    { id: 2, title: '인간 숫자 인식', path: '/number-model' }
  ];

  return (
    <div style={styles.wrapper}>
      <div style={styles.container}>
        <h1>🧠 인간지능 (Human Intelligence)</h1>
        <p>원하는 프로젝트를 선택하세요.</p>
        
        <div style={styles.grid}>
          {features.map((feature) => (
            <button 
              key={feature.id} 
              style={styles.button}
              onClick={() => navigate(feature.path)}
            >
              {feature.title}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}

// 2. 임시 페이지 컴포넌트
function FeaturePage ({ title }) {
  const navigate = useNavigate();
  return (
    <div style={styles.wrapper}>
      <div style={styles.container}>
        <h2>{title} 페이지입니다.</h2>
        <p>아직 백엔드 기능이 구현되지 않았습니다.</p>
        <button onClick={() => navigate('/')} style={styles.backButton}>
          메인으로 돌아가기
        </button>
      </div>
    </div>
  );
}

// 3. 앱 메인 라우터 설정
function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/imageGen-model" element={<TalkGenPage title="인간 사진 생성" />} />
        <Route path="/number-model" element={<FeaturePage title="인간 숫자 인식" />} />
      </Routes>
    </BrowserRouter>
  );
}

// CSS 스타일 설정
const styles = {
  // 화면 전체를 덮고 내용물을 정중앙에 배치하는 래퍼(Wrapper)
  wrapper: {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    width: '100vw',
    height: '100vh',
    backgroundColor: '#f8f9fa',
    margin: 0,
    padding: 0,
    boxSizing: 'border-box'
  },
  // 내용물들을 세로로 정렬하고 글자를 가운데로 맞추는 컨테이너
  container: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    textAlign: 'center',
    fontFamily: 'sans-serif',
  },
  // 3열 그리드 레이아웃 유지
  grid: {
    display: 'grid',
    gridTemplateColumns: 'repeat(3, 1fr)',
    gap: '20px',
    marginTop: '40px'
  },
  button: {
    padding: '20px 30px',
    fontSize: '16px',
    fontWeight: 'bold',
    cursor: 'pointer',
    backgroundColor: '#ffffff',
    border: '2px solid #333',
    borderRadius: '10px',
    transition: 'all 0.2s',
  },
  backButton: {
    padding: '10px 20px', 
    marginTop: '20px',
    fontSize: '15px',
    cursor: 'pointer',
    backgroundColor: '#333',
    color: '#fff',
    border: 'none',
    borderRadius: '5px'
  }
};

export default App;