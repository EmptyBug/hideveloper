import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function TalkGenPage() {
  const navigate = useNavigate();
  
  // 상태(State) 관리: 이제 이미지는 여러 개이므로 배열([])로, 세대 숫자도 저장합니다.
  const [images, setImages] = useState([]);
  const [generation, setGeneration] = useState(1);

  const fetchImages = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/image/noise');
      const data = await response.json();
      
      // 서버에서 준 { generation: 1, images: [...] } 데이터를 각각 저장
      setImages(data.images);
      setGeneration(data.generation);
    } catch (error) {
      console.error("백엔드와 연결할 수 없습니다.", error);
    }
  };

  useEffect(() => {
    fetchImages();
  }, []);

  return (
    <div style={styles.wrapper}>
      <div style={styles.container}>
        <h2>🧬 유전 알고리즘 (현재: {generation}세대)</h2>
        <p>가장 마음에 드는 이미지를 하나 선택(투표)해 주세요!</p>
        
        {/* 3개의 이미지가 가로로 나열되는 구역 */}
        <div style={styles.imageGrid}>
          {images.length > 0 ? (
            images.map((imgUrl, index) => (
              <div key={index} style={styles.imageCard}>
                <img 
                  src={imgUrl} 
                  alt={`${generation}세대 이미지 ${index + 1}`} 
                  style={styles.noiseImage} 
                />
                <button style={styles.voteButton}>
                  {index + 1}번 선택
                </button>
              </div>
            ))
          ) : (
            <p>서버에서 세대 이미지를 불러오는 중...</p>
          )}
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
    display: 'flex', justifyContent: 'center', alignItems: 'center',
    width: '100vw', height: '100vh', backgroundColor: '#f8f9fa',
    margin: 0, padding: 0, boxSizing: 'border-box'
  },
  container: {
    display: 'flex', flexDirection: 'column', alignItems: 'center',
    textAlign: 'center', fontFamily: 'sans-serif',
  },
  // 3개의 카드를 가로로 배치하고 간격을 줍니다.
  imageGrid: {
    display: 'flex', gap: '30px', margin: '30px 0'
  },
  // 각 이미지와 투표 버튼을 감싸는 예쁜 흰색 카드
  imageCard: {
    display: 'flex', flexDirection: 'column', alignItems: 'center',
    backgroundColor: '#fff', padding: '15px', borderRadius: '15px',
    boxShadow: '0 4px 8px rgba(0,0,0,0.1)'
  },
  noiseImage: {
    width: '200px', height: '200px', objectFit: 'cover', 
    borderRadius: '8px', marginBottom: '15px'
  },
  voteButton: {
    padding: '10px 20px', fontSize: '16px', fontWeight: 'bold', cursor: 'pointer',
    backgroundColor: '#4CAF50', color: 'white', border: 'none', borderRadius: '8px',
    width: '100%', transition: '0.2s'
  },
  backButton: {
    padding: '10px 20px', fontSize: '15px', cursor: 'pointer',
    backgroundColor: '#333', color: '#fff', border: 'none',
    borderRadius: '5px', marginTop: '20px'
  }
};

export default TalkGenPage;