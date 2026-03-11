import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

function TalkGenPage() {
  const navigate = useNavigate();
  
  const [images, setImages] = useState([]);
  const [generation, setGeneration] = useState(1);
  
  // ★ 방금 투표한 번호를 기억할 새로운 상태 (초기값은 null)
  const [lastVoted, setLastVoted] = useState(null);

  const fetchImages = async () => {
    try {
      const response = await fetch('http://localhost:8080/api/image/noise'); // 백엔드 포트에 맞게 수정되어 있다고 가정 (예: 5173 또는 8080)
      const data = await response.json();
      
      if (Array.isArray(data.images)) {
        // 세대가 바뀌면 이전 투표 기록 메세지를 지워줍니다.
        if (data.generation !== generation) {
          setLastVoted(null); 
        }
        setImages(data.images);
        setGeneration(data.generation);
      }
    } catch (error) {
      console.error("백엔드와 연결할 수 없습니다.", error);
    }
  };

  const handleVote = async (selectedIndex) => {
    try {
      const voteData = {
        generation: generation,
        selectedIndex: selectedIndex
      };

      const response = await fetch('http://localhost:8080/api/vote', { // 백엔드 포트에 맞게 수정되어 있다고 가정
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(voteData),
      });

      if (response.ok) {
        // ★ alert 창을 지우고, 방금 누른 번호를 상태에 저장합니다.
        setLastVoted(selectedIndex);
      } else {
        alert("투표 처리에 실패했습니다."); // 에러일 때만 알림을 띄웁니다.
      }
    } catch (error) {
      console.error("투표 전송 중 오류 발생:", error);
    }
  };

  useEffect(() => {
    fetchImages();
    const pollingInterval = setInterval(() => {
      fetchImages();
    }, 3000);
    return () => clearInterval(pollingInterval);
  }, [generation]); // generation 값이 바뀔 때마다 useEffect 내부 로직이 최신 값을 참조하도록 의존성 배열 업데이트

  return (
    <div style={styles.wrapper}>
      <div style={styles.container}>
        <h2>🧬 유전 알고리즘 (현재: {generation}세대)</h2>
        <p>가장 마음에 드는 이미지를 하나 선택(투표)해 주세요!</p>
        
        <div style={styles.imageGrid}>
          {Array.isArray(images) && images.length > 0 ? (
            images.map((imgUrl, index) => (
              <div key={index} style={styles.imageCard}>
                <img 
                  src={imgUrl} 
                  alt={`${generation}세대 이미지 ${index + 1}`} 
                  style={styles.noiseImage} 
                />
                <button 
                  style={styles.voteButton}
                  onClick={() => handleVote(index + 1)}
                >
                  {index + 1}번 선택
                </button>
              </div>
            ))
          ) : (
            <p>서버에서 세대 이미지를 불러오는 중...</p>
          )}
        </div>
        
        {/* ★ 메인으로 돌아가기 버튼 바로 위: 투표 완료 메세지 표시 영역 */}
        {lastVoted && (
          <div style={styles.voteMessage}>
            ✅ 방금 {lastVoted}번 이미지에 투표했습니다!
          </div>
        )}
        
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
  imageGrid: {
    display: 'flex', gap: '30px', margin: '30px 0'
  },
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
  // ★ 추가된 메세지 스타일 (연한 초록색 배경에 진한 초록색 글씨)
  voteMessage: {
    marginTop: '10px',
    padding: '10px 20px',
    backgroundColor: '#e6f4ea',
    color: '#137333',
    borderRadius: '8px',
    fontWeight: 'bold',
    fontSize: '15px'
  },
  backButton: {
    padding: '10px 20px', fontSize: '15px', cursor: 'pointer',
    backgroundColor: '#333', color: '#fff', border: 'none',
    borderRadius: '5px', marginTop: '20px' // 여백 살짝 조정
  }
};

export default TalkGenPage;