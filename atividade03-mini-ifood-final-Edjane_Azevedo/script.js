const API = 'http://localhost:8080';

let USUARIO = "";
let SENHA = "";
function fazerLogin() {
  USUARIO = loginUser.value.trim();
  SENHA = loginPass.value.trim();

  if (!USUARIO || !SENHA) {
    alert("Preencha usuário e senha");
    return;
  }

  localStorage.setItem("usuario", USUARIO);
  localStorage.setItem("senha", SENHA);

  loginBox.style.display = "none";
  sistema.style.display = "block";

  mostrarResultado({ mensagem: "Login realizado com sucesso!" });
}

function logout() {
  USUARIO = "";
  SENHA = "";

  localStorage.removeItem("usuario");
  localStorage.removeItem("senha");

  loginUser.value = "";
  loginPass.value = "";

  loginBox.style.display = "block";
  sistema.style.display = "none";
}

function getAuthHeader() {
  return "Basic " + btoa(USUARIO + ":" + SENHA);
}
window.onload = function () {
  const usuarioSalvo = localStorage.getItem("usuario");
  const senhaSalva = localStorage.getItem("senha");

  if (usuarioSalvo && senhaSalva) {
    USUARIO = usuarioSalvo;
    SENHA = senhaSalva;

    loginBox.style.display = "none";
    sistema.style.display = "block";

    mostrarResultado({ mensagem: "Login recuperado automaticamente." });
  }
};

function mostrarResultado(dados) {
  const elemento = document.getElementById("resultado");

  if (!elemento) {
    console.error("Elemento resultado não encontrado.");
    return;
  }

  if (dados instanceof Error) {
    elemento.textContent = dados.message;
    return;
  }

  if (!dados || Object.keys(dados).length === 0) {
    elemento.textContent = "Operação realizada com sucesso!";
    return;
  }

  elemento.textContent = JSON.stringify(dados, null, 2);
}

async function requisicao(url, opcoes = {}) {
  const resposta = await fetch(url, {
    headers: {
      "Content-Type": "application/json",
      "Authorization": getAuthHeader()
    },
    ...opcoes
  });

  const texto = await resposta.text();

  let dados = {};
  if (texto) {
    try {
      dados = JSON.parse(texto);
    } catch {
      dados = { resposta: texto };
    }
  }

  if (!resposta.ok) {
    throw dados;
  }

  return dados;
}

function mostrarLista(lista, tipo) {
  const div = document.getElementById("lista");

  if (!div) {
    mostrarResultado({ erro: "Elemento lista não encontrado no HTML." });
    return;
  }

  if (!Array.isArray(lista) || lista.length === 0) {
    div.innerHTML = '<div class="item">Nenhum registro encontrado.</div>';
    return;
  }

  div.innerHTML = lista.map(item => {
    if (tipo === "clientes") {
      return `
        <div class="item">
          <span class="badge">Cliente</span>
          <div class="item-title">${item.nome ?? ""}</div>
          <div class="muted">
            ID: ${item.id ?? ""}<br>
            Email: ${item.email ?? ""}<br>
            Pedidos: ${item.quantidadePedidos ?? 0}
          </div>
          <button onclick='preencherCliente(${JSON.stringify(item)})'>Editar</button>
          <button class="btn-danger" onclick="deletarRegistro('clientes', ${item.id}, listarClientes)">Excluir</button>
        </div>`;
    }

    if (tipo === "restaurantes") {
      return `
        <div class="item">
          <span class="badge">Restaurante</span>
          <div class="item-title">${item.nome ?? ""}</div>
          <div class="muted">
            ID: ${item.id ?? ""}<br>
            Categoria: ${item.categoria ?? ""}<br>
            Produtos: ${item.quantidadeProdutos ?? 0}
          </div>
          <button onclick='preencherRestaurante(${JSON.stringify(item)})'>Editar</button>
          <button class="btn-danger" onclick="deletarRegistro('restaurantes', ${item.id}, listarRestaurantes)">Excluir</button>
        </div>`;
    }

    if (tipo === "produtos") {
      return `
        <div class="item">
          <span class="badge">Produto</span>
          <div class="item-title">${item.nome ?? ""}</div>
          <div class="muted">
            ID: ${item.id ?? ""}<br>
            Preço: R$ ${item.preco ?? ""}<br>
            Restaurante: ${item.restauranteNome ?? ""} (ID ${item.restauranteId ?? ""})
          </div>
          <button onclick='preencherProduto(${JSON.stringify(item)})'>Editar</button>
          <button class="btn-danger" onclick="deletarRegistro('produtos', ${item.id}, listarProdutos)">Excluir</button>
        </div>`;
    }

    return `
      <div class="item">
        <span class="badge">Pedido</span>
        <div class="item-title">Pedido #${item.id ?? ""}</div>
        <div class="muted">
          Status: ${item.status ?? ""}<br>
          Cliente: ${item.clienteNome ?? ""}<br>
          Valor total: R$ ${item.valorTotal ?? ""}<br>
          Produtos: ${Array.isArray(item.produtos) ? item.produtos.join(", ") : ""}
        </div>
        <button onclick='preencherPedido(${JSON.stringify(item)})'>Editar</button>
        <button class="btn-danger" onclick="deletarRegistro('pedidos', ${item.id}, listarPedidos)">Excluir</button>
      </div>`;
  }).join("");
}

async function deletarRegistro(tipo, id, refresh) {
  if (!confirm("Excluir registro?")) return;

  try {
    const r = await fetch(`${API}/${tipo}/${id}`, {
      method: "DELETE",
      headers: { "Authorization": getAuthHeader() }
    });

    if (r.status === 204) {
      mostrarResultado({ mensagem: "Excluído com sucesso" });
      await refresh();
      return;
    }

    mostrarResultado(await r.json().catch(() => ({ erro: "Erro ao excluir" })));

  } catch (e) {
    mostrarResultado({ erro: "Erro ao excluir", detalhes: e.message });
  }
}

async function buscarClientePorNome(nome) {
  const r = await fetch(`${API}/clientes?nome=${encodeURIComponent(nome)}`, {
    headers: { "Authorization": getAuthHeader() }
  });

  const texto = await r.text();

  let clientes = [];
  if (texto) {
    try {
      clientes = JSON.parse(texto);
    } catch {
      throw { erro: "Resposta inválida da API" };
    }
  }

  if (!r.ok) throw clientes;

  if (!clientes.length) {
    throw { erro: "Cliente não encontrado" };
  }

  return clientes[0];
}

function limparCliente() {
  clienteId.value = "";
  clienteNome.value = "";
  clienteEmail.value = "";
}

function limparRestaurante() {
  restauranteId.value = "";
  restauranteNome.value = "";
  restauranteCategoria.value = "";
}

function limparProduto() {
  produtoId.value = "";
  produtoNome.value = "";
  produtoPreco.value = "";
  produtoRestauranteId.value = "";
}

function limparPedido() {
  pedidoId.value = "";
  pedidoClienteNome.value = "";
  pedidoProdutosIds.value = "";
  pedidoStatus.value = "CRIADO";
}

async function criarCliente() {
  try {
    const d = await requisicao(`${API}/clientes`, {
      method: "POST",
      body: JSON.stringify({
        nome: clienteNome.value,
        email: clienteEmail.value
      })
    });

    mostrarResultado(d);
    limparCliente();
    await listarClientes();

  } catch (e) {
    mostrarResultado(e);
  }
}

async function listarClientes() {
  try {
    const dados = await requisicao(`${API}/clientes`);
    mostrarLista(dados, "clientes");
  } catch (e) {
    mostrarResultado(e);
  }
}

function preencherCliente(i) {
  clienteId.value = i.id ?? "";
  clienteNome.value = i.nome ?? "";
  clienteEmail.value = i.email ?? "";
}

async function atualizarCliente() {
  try {
    if (!clienteId.value) {
      mostrarResultado({ erro: "Clique em Editar antes" });
      return;
    }

    const d = await requisicao(`${API}/clientes/${clienteId.value}`, {
      method: "PUT",
      body: JSON.stringify({
        nome: clienteNome.value,
        email: clienteEmail.value
      })
    });

    mostrarResultado(d);
    limparCliente();
    await listarClientes();

  } catch (e) {
    mostrarResultado(e);
  }
}

async function criarRestaurante() {
  try {
    const d = await requisicao(`${API}/restaurantes`, {
      method: "POST",
      body: JSON.stringify({
        nome: restauranteNome.value,
        categoria: restauranteCategoria.value
      })
    });

    mostrarResultado(d);
    limparRestaurante();
    await listarRestaurantes();

  } catch (e) {
    mostrarResultado(e);
  }
}

async function listarRestaurantes() {
  try {
    const dados = await requisicao(`${API}/restaurantes`);
    mostrarLista(dados, "restaurantes");
  } catch (e) {
    mostrarResultado(e);
  }
}

function preencherRestaurante(i) {
  restauranteId.value = i.id ?? "";
  restauranteNome.value = i.nome ?? "";
  restauranteCategoria.value = i.categoria ?? "";
}

async function atualizarRestaurante() {
  try {
    if (!restauranteId.value) {
      mostrarResultado({ erro: "Clique em Editar antes" });
      return;
    }

    const d = await requisicao(`${API}/restaurantes/${restauranteId.value}`, {
      method: "PUT",
      body: JSON.stringify({
        nome: restauranteNome.value,
        categoria: restauranteCategoria.value
      })
    });

    mostrarResultado(d);
    limparRestaurante();
    await listarRestaurantes();

  } catch (e) {
    mostrarResultado(e);
  }
}

async function criarProduto() {
  try {
    const d = await requisicao(`${API}/produtos`, {
      method: "POST",
      body: JSON.stringify({
        nome: produtoNome.value,
        preco: parseFloat(produtoPreco.value),
        restauranteId: parseInt(produtoRestauranteId.value)
      })
    });

    mostrarResultado(d);
    limparProduto();
    await listarProdutos();

  } catch (e) {
    mostrarResultado(e);
  }
}

async function listarProdutos() {
  try {
    const dados = await requisicao(`${API}/produtos`);
    mostrarLista(dados, "produtos");
  } catch (e) {
    mostrarResultado(e);
  }
}

function preencherProduto(i) {
  produtoId.value = i.id ?? "";
  produtoNome.value = i.nome ?? "";
  produtoPreco.value = i.preco ?? "";
  produtoRestauranteId.value = i.restauranteId ?? "";
}

async function atualizarProduto() {
  try {
    if (!produtoId.value) {
      mostrarResultado({ erro: "Clique em Editar antes" });
      return;
    }

    const d = await requisicao(`${API}/produtos/${produtoId.value}`, {
      method: "PUT",
      body: JSON.stringify({
        nome: produtoNome.value,
        preco: parseFloat(produtoPreco.value),
        restauranteId: parseInt(produtoRestauranteId.value)
      })
    });

    mostrarResultado(d);
    limparProduto();
    await listarProdutos();

  } catch (e) {
    mostrarResultado(e);
  }
}

async function criarPedido() {
  try {
    const c = await buscarClientePorNome(pedidoClienteNome.value);

    const ids = pedidoProdutosIds.value
      .split(",")
      .map(v => parseInt(v.trim()))
      .filter(v => !Number.isNaN(v));

    const d = await requisicao(`${API}/pedidos`, {
      method: "POST",
      body: JSON.stringify({
        clienteId: c.id,
        status: pedidoStatus.value,
        produtosIds: ids
      })
    });

    mostrarResultado(d);
    limparPedido();
    await listarPedidos();

  } catch (e) {
    mostrarResultado(e);
  }
}

async function listarPedidos() {
  try {
    const dados = await requisicao(`${API}/pedidos`);
    mostrarLista(dados, "pedidos");
  } catch (e) {
    mostrarResultado(e);
  }
}

function preencherPedido(i) {
  pedidoId.value = i.id ?? "";
  pedidoClienteNome.value = i.clienteNome ?? "";
  pedidoStatus.value = i.status ?? "CRIADO";
  pedidoProdutosIds.value = "";

  mostrarResultado({
    aviso: "Digite novamente os IDs dos produtos antes de atualizar.",
    pedidoSelecionado: i
  });
}

async function atualizarPedido() {
  try {
    if (!pedidoId.value) {
      mostrarResultado({ erro: "Clique em Editar antes" });
      return;
    }

    const c = await buscarClientePorNome(pedidoClienteNome.value);

    const ids = pedidoProdutosIds.value
      .split(",")
      .map(v => parseInt(v.trim()))
      .filter(v => !Number.isNaN(v));

    const d = await requisicao(`${API}/pedidos/${pedidoId.value}`, {
      method: "PUT",
      body: JSON.stringify({
        clienteId: c.id,
        status: pedidoStatus.value,
        produtosIds: ids
      })
    });

    mostrarResultado(d);
    limparPedido();
    await listarPedidos();

  } catch (e) {
    mostrarResultado(e);
  }
}