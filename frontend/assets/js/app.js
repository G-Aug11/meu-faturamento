const API = 'http://localhost:8080/api';

function toast(msg, tipo) {
  tipo = tipo || 'sucesso';
  var c = document.getElementById('toast-container');
  if (!c) { c = document.createElement('div'); c.id = 'toast-container'; c.className = 'toast-container'; document.body.appendChild(c); }
  var el = document.createElement('div');
  el.className = 'toast' + (tipo === 'erro' ? ' erro' : tipo === 'aviso' ? ' aviso' : '');
  el.textContent = (tipo === 'sucesso' ? '✅ ' : tipo === 'erro' ? '❌ ' : '⚠️ ') + msg;
  c.appendChild(el);
  setTimeout(function(){ el.style.opacity='0'; setTimeout(function(){ el.remove(); },400); }, 3500);
}

async function apiGet(ep, params) {
  params = params || {};
  var url = new URL(API + ep);
  Object.entries(params).forEach(function(e){ url.searchParams.set(e[0], e[1]); });
  var r = await fetch(url.toString());
  if (!r.ok) throw new Error('HTTP ' + r.status);
  return r.json();
}

async function apiPost(ep, body) {
  var r = await fetch(API + ep, { method:'POST', headers:{'Content-Type':'application/json'}, body:JSON.stringify(body) });
  return r.json();
}

async function apiPut(ep, body) {
  var r = await fetch(API + ep, { method:'PUT', headers:{'Content-Type':'application/json'}, body:JSON.stringify(body) });
  return r.json();
}

async function apiDelete(ep) {
  var r = await fetch(API + ep, { method:'DELETE' });
  return r.json();
}

function formatBRL(v) {
  return new Intl.NumberFormat('pt-BR', { style:'currency', currency:'BRL' }).format(Number(v) || 0);
}

function formatData(iso) {
  if (!iso || iso === '') return '—';
  try {
    var d = new Date(iso);
    if (isNaN(d.getTime())) return '—';
    return d.toLocaleDateString('pt-BR') + ' ' + d.toLocaleTimeString('pt-BR', { hour:'2-digit', minute:'2-digit' });
  } catch(e) { return '—'; }
}

function formatForma(forma) {
  var map = { dinheiro:'💵 Dinheiro', pix:'⚡ Pix', cartao_debito:'💳 Débito', cartao_credito:'💳 Crédito' };
  return map[forma] || forma || '—';
}

function iniciarLogo() {
  var logo = localStorage.getItem('rickBarberLogo');
  if (logo) aplicarLogo(logo);
  var inp = document.getElementById('input-logo');
  if (!inp) return;
  inp.addEventListener('change', function() {
    var file = this.files[0]; if (!file) return;
    var reader = new FileReader();
    reader.onload = function(e) {
      localStorage.setItem('rickBarberLogo', e.target.result);
      aplicarLogo(e.target.result);
      toast('Logo atualizada!');
    };
    reader.readAsDataURL(file);
  });
}

function aplicarLogo(src) {
  var ph = document.getElementById('logo-placeholder');
  if (ph) ph.innerHTML = '<img style="width:100%;height:100%;object-fit:cover;border-radius:10px;" src="' + src + '" alt="Logo">';
}

var EMOJIS = ['✂️','💈','🪒','💇','🧴','🪮','💆','🧔','👱','🌟','⭐','🔥','💎','🏆','👑','🎯','🦁','🦅','🌿','❤️','🖤','💛','💪','✨','⚡','🧼','🎩','👒','🕶️','🥇'];

function montarEmojiGrid(containerId, emojiAtual, onSelect) {
  var grid = document.getElementById(containerId);
  if (!grid) return;
  grid.innerHTML = EMOJIS.map(function(e) {
    return '<div class="emoji-opt ' + (e === emojiAtual ? 'ativo' : '') + '" data-e="' + e + '">' + e + '</div>';
  }).join('');
  grid.querySelectorAll('.emoji-opt').forEach(function(el) {
    el.addEventListener('click', function() {
      grid.querySelectorAll('.emoji-opt').forEach(function(x){ x.classList.remove('ativo'); });
      el.classList.add('ativo');
      onSelect(el.dataset.e);
    });
  });
}

document.addEventListener('DOMContentLoaded', function() {
  iniciarLogo();
  var cur = location.pathname.split('/').pop() || 'index.html';
  document.querySelectorAll('.sidebar__nav a').forEach(function(a) {
    if (a.getAttribute('href').split('/').pop() === cur) a.classList.add('active');
  });
});

// ── Autenticação ─────────────────────────────────────────────────────────────

function getToken() {
  return sessionStorage.getItem('auth_token');
}

function checarLogin() {
  if (!getToken()) {
    window.location.href = 'login.html';
    return false;
  }
  return true;
}

function logout() {
  sessionStorage.removeItem('auth_token');
  sessionStorage.removeItem('auth_usuario');
  window.location.href = 'login.html';
}

// Sobrescreve apiGet/apiPost/apiPut/apiDelete com header Authorization
async function apiGet(ep, params) {
  if (!checarLogin()) return;
  params = params || {};
  var url = new URL(API + ep);
  Object.entries(params).forEach(function(e){ url.searchParams.set(e[0], e[1]); });
  var r = await fetch(url.toString(), {
    headers: { 'Authorization': 'Bearer ' + getToken() }
  });
  if (r.status === 401) { logout(); return; }
  if (!r.ok) throw new Error('HTTP ' + r.status);
  return r.json();
}

async function apiPost(ep, body) {
  if (!checarLogin()) return;
  var r = await fetch(API + ep, {
    method:'POST',
    headers:{'Content-Type':'application/json', 'Authorization': 'Bearer ' + getToken()},
    body:JSON.stringify(body)
  });
  if (r.status === 401) { logout(); return; }
  return r.json();
}

async function apiPut(ep, body) {
  if (!checarLogin()) return;
  var r = await fetch(API + ep, {
    method:'PUT',
    headers:{'Content-Type':'application/json', 'Authorization': 'Bearer ' + getToken()},
    body:JSON.stringify(body)
  });
  if (r.status === 401) { logout(); return; }
  return r.json();
}

async function apiDelete(ep) {
  if (!checarLogin()) return;
  var r = await fetch(API + ep, {
    method:'DELETE',
    headers:{ 'Authorization': 'Bearer ' + getToken() }
  });
  if (r.status === 401) { logout(); return; }
  return r.json();
}

// ── Microsserviço de agendamentos (porta 8081) ───────────────────────────────

const API_AGENDA = 'http://localhost:8081/api';

async function agendaFetch(metodo, ep, body) {
  if (!checarLogin()) return;
  var opcoes = { method: metodo, headers: { 'Authorization': 'Bearer ' + getToken() } };
  if (body) {
    opcoes.headers['Content-Type'] = 'application/json';
    opcoes.body = JSON.stringify(body);
  }
  try {
    var r = await fetch(API_AGENDA + ep, opcoes);
    if (r.status === 401) { logout(); return; }
    var txt = await r.text();
    return txt ? JSON.parse(txt) : { erro: 'Erro ' + r.status };
  } catch (e) {
    // cai aqui se o agendamento-service não estiver rodando
    return { erro: 'Serviço de agenda fora do ar. Ele está rodando na porta 8081?' };
  }
}
