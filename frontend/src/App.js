import React, { useState } from 'react';
import './App.css';

const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

function CriterionCard({ item }) {
  return (
    <div className={`criterion-card level-${item.level}`}>
      <div className="criterion-header">
        <span className="criterion-name">{item.criterion}</span>
        <span className={`level-badge ${item.level}`}>{item.level}</span>
      </div>
      <p className="criterion-feedback">{item.feedback}</p>
    </div>
  );
}

function ListSection({ title, items }) {
  return (
    <div className="list-section">
      <h3>{title}</h3>
      <ul>
        {items.map((item, i) => (
          <li key={i}>{item}</li>
        ))}
      </ul>
    </div>
  );
}

function App() {
  const [text, setText] = useState('');
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const evaluate = async () => {
    if (!text.trim()) {
      setError('Indtast en opgavetekst før evaluering.');
      return;
    }
    setLoading(true);
    setError('');
    setResult(null);

    try {
      const response = await fetch(`${API_URL}/api/evaluations`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ text }),
      });

      const data = await response.json();

      if (!response.ok) {
        throw new Error(data.error || 'Ukendt fejl fra serveren');
      }

      setResult(data);
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="app">
      <header>
        <h1>AI Praktikrapport-vurdering</h1>
        <p>Indsæt en praktikrapport og få en vejledende AI-baseret vurdering.</p>
      </header>

      <div className="input-card">
        <label htmlFor="report-text">Praktikrapport</label>
        <textarea
          id="report-text"
          rows={16}
          value={text}
          onChange={e => setText(e.target.value)}
          placeholder="Indsæt rapportteksten her..."
        />
        <button onClick={evaluate} disabled={loading}>
          {loading ? 'Vurderer...' : 'Evaluér'}
        </button>
        {error && <p className="error-msg">{error}</p>}
      </div>

      {result && (
        <div className="result-card">
          <h2>Vejledende vurdering</h2>

          <div className="overall-assessment">
            {result.overallAssessment}
          </div>

          <p className="section-title">Kriterier</p>
          <div className="criteria-list">
            {result.criteriaFeedback.map((item, i) => (
              <CriterionCard key={i} item={item} />
            ))}
          </div>

          <div className="lists-grid">
            <ListSection title="Styrker" items={result.strengths} />
            <ListSection title="Svagheder" items={result.weaknesses} />
            <ListSection title="Forbedringsforslag" items={result.improvements} />
            <ListSection title="Spørgsmål til dialog" items={result.questions} />
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
