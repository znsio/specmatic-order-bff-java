async function fetchProducts(type) {
    const response = await fetch("http://localhost:8082/findAvailableProducts?type=" + type);
    console.log('GOT PRODUCTS');
    return await response.json();
}

export async function showProducts() {
    console.log('FETCHING PRODUCTS');
    var products = await fetchProducts("gadget");
    console.log('SHOWING PRODUCTS');
    console.log(products);
    const productsDiv = document.getElementById("products");
    const rows = products.map(product => `<div class="row data-row"><div class="col bg-light text-black">${product.id}</div><div class="col bg-light text-black">${product.name}</div></div>`).join("\n");
    const header = '<div class="row"><div class="col bg-secondary text-white">Id</div><div class="col bg-secondary text-white">Name</div></div>';
    productsDiv.innerHTML = productsDiv.innerHTML + `${header}\n${rows}`
}
