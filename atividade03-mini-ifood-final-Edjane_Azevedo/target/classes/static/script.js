const API='http://localhost:8080';
let USUARIO='', SENHA='';

function fazerLogin(){
  USUARIO=loginUser.value; SENHA=loginPass.value;
  if(!USUARIO || !SENHA){alert('Preencha usuário e senha'); return;}
  loginBox.style.display='none'; sistema.style.display='block';
}
function logout(){USUARIO='';SENHA='';loginUser.value='';loginPass.value='';loginBox.style.display='block';sistema.style.display='none';}
function getAuthHeader(){return 'Basic '+btoa(USUARIO+':'+SENHA);}
function mostrarResultado(d){resultado.textContent=JSON.stringify(d,null,2);}

async function requisicao(url,opcoes={}){
 const resposta=await fetch(url,{headers:{'Content-Type':'application/json','Authorization':getAuthHeader()},...opcoes});
 const dados=await resposta.json().catch(()=>({}));
 if(!resposta.ok) throw dados;
 return dados;
}
async function deletarRegistro(tipo,id,refresh){
 if(!confirm('Excluir registro?')) return;
 try{
  const r=await fetch(`${API}/${tipo}/${id}`,{method:'DELETE',headers:{'Authorization':getAuthHeader()}});
  if(r.status===204){mostrarResultado({mensagem:'Excluído com sucesso'});refresh();return;}
  mostrarResultado(await r.json().catch(()=>({erro:'Erro ao excluir'})));
 }catch(e){mostrarResultado({erro:'Erro ao excluir',detalhes:e.message});}
}
function mostrarLista(lista,tipo){
 if(!Array.isArray(lista)||lista.length===0){document.getElementById('lista').innerHTML='<div class="item">Nenhum registro encontrado.</div>';return;}
 document.getElementById('lista').innerHTML=lista.map(item=>{
  if(tipo==='clientes')return `<div class="item"><span class="badge">Cliente</span><div class="item-title">${item.nome??''}</div><div class="muted">ID: ${item.id}<br>Email: ${item.email??''}<br>Pedidos: ${item.quantidadePedidos??0}</div><button onclick='preencherCliente(${JSON.stringify(item)})'>Editar</button><button class="btn-danger" onclick="deletarRegistro('clientes',${item.id},listarClientes)">Excluir</button></div>`;
  if(tipo==='restaurantes')return `<div class="item"><span class="badge">Restaurante</span><div class="item-title">${item.nome??''}</div><div class="muted">ID: ${item.id}<br>Categoria: ${item.categoria??''}<br>Produtos: ${item.quantidadeProdutos??0}</div><button onclick='preencherRestaurante(${JSON.stringify(item)})'>Editar</button><button class="btn-danger" onclick="deletarRegistro('restaurantes',${item.id},listarRestaurantes)">Excluir</button></div>`;
  if(tipo==='produtos')return `<div class="item"><span class="badge">Produto</span><div class="item-title">${item.nome??''}</div><div class="muted">ID: ${item.id}<br>Preço: R$ ${item.preco??''}<br>Restaurante: ${item.restauranteNome??''} (ID ${item.restauranteId??''})</div><button onclick='preencherProduto(${JSON.stringify(item)})'>Editar</button><button class="btn-danger" onclick="deletarRegistro('produtos',${item.id},listarProdutos)">Excluir</button></div>`;
  return `<div class="item"><span class="badge">Pedido</span><div class="item-title">Pedido #${item.id??''}</div><div class="muted">Status: ${item.status??''}<br>Cliente: ${item.clienteNome??''} (ID ${item.clienteId??''})<br>Valor total: R$ ${item.valorTotal??''}<br>Produtos: ${Array.isArray(item.produtos)?item.produtos.join(', '):''}</div><button onclick='preencherPedido(${JSON.stringify(item)})'>Editar</button><button class="btn-danger" onclick="deletarRegistro('pedidos',${item.id},listarPedidos)">Excluir</button></div>`;
 }).join('');
}

async function buscarClientePorNome(nome){
 const r=await fetch(`${API}/clientes?nome=${encodeURIComponent(nome)}`,{headers:{'Authorization':getAuthHeader()}});
 const clientes=await r.json();
 if(!clientes.length) throw {erro:'Cliente não encontrado'};
 return clientes[0];
}
function limparCliente(){clienteId.value='';clienteNome.value='';clienteEmail.value='';}
function limparRestaurante(){restauranteId.value='';restauranteNome.value='';restauranteCategoria.value='';}
function limparProduto(){produtoId.value='';produtoNome.value='';produtoPreco.value='';produtoRestauranteId.value='';}
function limparPedido(){pedidoId.value='';pedidoClienteNome.value='';pedidoProdutosIds.value='';pedidoStatus.value='CRIADO';}

async function criarCliente(){try{const d=await requisicao(`${API}/clientes`,{method:'POST',body:JSON.stringify({nome:clienteNome.value,email:clienteEmail.value})});mostrarResultado(d);limparCliente();listarClientes();}catch(e){mostrarResultado(e);}}
async function listarClientes(){try{mostrarLista(await requisicao(`${API}/clientes`),'clientes');}catch(e){mostrarResultado(e);}}
function preencherCliente(i){clienteId.value=i.id??'';clienteNome.value=i.nome??'';clienteEmail.value=i.email??'';}
async function atualizarCliente(){try{if(!clienteId.value)return mostrarResultado({erro:'Clique em Editar antes'});const d=await requisicao(`${API}/clientes/${clienteId.value}`,{method:'PUT',body:JSON.stringify({nome:clienteNome.value,email:clienteEmail.value})});mostrarResultado(d);limparCliente();listarClientes();}catch(e){mostrarResultado(e);}}

async function criarRestaurante(){try{const d=await requisicao(`${API}/restaurantes`,{method:'POST',body:JSON.stringify({nome:restauranteNome.value,categoria:restauranteCategoria.value})});mostrarResultado(d);limparRestaurante();listarRestaurantes();}catch(e){mostrarResultado(e);}}
async function listarRestaurantes(){try{mostrarLista(await requisicao(`${API}/restaurantes`),'restaurantes');}catch(e){mostrarResultado(e);}}
function preencherRestaurante(i){restauranteId.value=i.id??'';restauranteNome.value=i.nome??'';restauranteCategoria.value=i.categoria??'';}
async function atualizarRestaurante(){try{if(!restauranteId.value)return mostrarResultado({erro:'Clique em Editar antes'});const d=await requisicao(`${API}/restaurantes/${restauranteId.value}`,{method:'PUT',body:JSON.stringify({nome:restauranteNome.value,categoria:restauranteCategoria.value})});mostrarResultado(d);limparRestaurante();listarRestaurantes();}catch(e){mostrarResultado(e);}}

async function criarProduto(){try{const d=await requisicao(`${API}/produtos`,{method:'POST',body:JSON.stringify({nome:produtoNome.value,preco:parseFloat(produtoPreco.value),restauranteId:parseInt(produtoRestauranteId.value)})});mostrarResultado(d);limparProduto();listarProdutos();}catch(e){mostrarResultado(e);}}
async function listarProdutos(){try{mostrarLista(await requisicao(`${API}/produtos`),'produtos');}catch(e){mostrarResultado(e);}}
function preencherProduto(i){produtoId.value=i.id??'';produtoNome.value=i.nome??'';produtoPreco.value=i.preco??'';produtoRestauranteId.value=i.restauranteId??'';}
async function atualizarProduto(){try{if(!produtoId.value)return mostrarResultado({erro:'Clique em Editar antes'});const d=await requisicao(`${API}/produtos/${produtoId.value}`,{method:'PUT',body:JSON.stringify({nome:produtoNome.value,preco:parseFloat(produtoPreco.value),restauranteId:parseInt(produtoRestauranteId.value)})});mostrarResultado(d);limparProduto();listarProdutos();}catch(e){mostrarResultado(e);}}

async function criarPedido(){try{const c=await buscarClientePorNome(pedidoClienteNome.value);const ids=pedidoProdutosIds.value.split(',').map(v=>parseInt(v.trim())).filter(v=>!Number.isNaN(v));const d=await requisicao(`${API}/pedidos`,{method:'POST',body:JSON.stringify({clienteId:c.id,status:pedidoStatus.value,produtosIds:ids})});mostrarResultado(d);limparPedido();listarPedidos();}catch(e){mostrarResultado(e);}}
async function listarPedidos(){try{mostrarLista(await requisicao(`${API}/pedidos`),'pedidos');}catch(e){mostrarResultado(e);}}
function preencherPedido(i){pedidoId.value=i.id??'';pedidoClienteNome.value=i.clienteNome??'';pedidoStatus.value=i.status??'CRIADO';pedidoProdutosIds.value='';mostrarResultado({aviso:'Digite novamente os IDs dos produtos antes de atualizar.',pedidoSelecionado:i});}
async function atualizarPedido(){try{if(!pedidoId.value)return mostrarResultado({erro:'Clique em Editar antes'});const c=await buscarClientePorNome(pedidoClienteNome.value);const ids=pedidoProdutosIds.value.split(',').map(v=>parseInt(v.trim())).filter(v=>!Number.isNaN(v));const d=await requisicao(`${API}/pedidos/${pedidoId.value}`,{method:'PUT',body:JSON.stringify({clienteId:c.id,status:pedidoStatus.value,produtosIds:ids})});mostrarResultado(d);limparPedido();listarPedidos();}catch(e){mostrarResultado(e);}}
