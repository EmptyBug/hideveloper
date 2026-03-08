import React from 'react';
import { useNavigate } from 'react-router-dom';

function TalkGenPage() {
  const navigate = useNavigate();

  return (
    <div style={styles.wrapper}>
      <div style={styles.container}>
        <h2>🗣️ 인간 언어 모델 (TalkGen)</h2>
        <p>이전 사람의 단어를 보고 다음 단어를 예측해 이어붙이는 공간입니다.</p>
        
        {/* 추후 여기에 단어 입력 UI와 실시간 채팅창이 들어갈 자리입니다. */}
        <div style={styles.chatPlaceholder}>
          입력창과 데이터가 들어갈 자리
        </div>
        
        <button onClick={() => navigate('/')} style={styles.backButton}>
          메인으로 돌아가기
        </button>
      </div>
    </div>
  );
}

const styles = {
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
  container: {
    display: 'flex',
    flexDirection: 'column',
    alignItems: 'center',
    textAlign: 'center',
    fontFamily: 'sans-serif',
  },
  chatPlaceholder: {
    width: '300px',
    height: '200px',
    margin: '20px 0',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#e9ecef',
    border: '2px dashed #ced4da',
    borderRadius: '10px',
    color: '#6c757d'
  },
  backButton: {
    padding: '10px 20px', 
    marginTop: '10px',
    fontSize: '15px',
    cursor: 'pointer',
    backgroundColor: '#333',
    color: '#fff',
    border: 'none',
    borderRadius: '5px'
  }
};

export default TalkGenPage;