const apiBase = '/api/files';

document.addEventListener('DOMContentLoaded', () => {
	const uploadForm = document.getElementById('uploadForm');
	const fileInput = document.getElementById('fileInput');
	const message = document.getElementById('message');
	const fileList = document.getElementById('fileList');

	uploadForm.addEventListener('submit', async (e) => {
		e.preventDefault();
		if (!fileInput.files.length) return;
		const formData = new FormData();
		formData.append('file', fileInput.files[0]);
		message.textContent = 'Wird hochgeladen...';
		try {
			const res = await fetch(apiBase + '/upload', {
				method: 'POST',
				body: formData
			});
			if (!res.ok) throw new Error('Upload fehlgeschlagen');
			message.textContent = 'Datei erfolgreich hochgeladen!';
			fileInput.value = '';
			await loadFiles();
		} catch (err) {
			message.textContent = err.message;
		}
	});

	async function loadFiles() {
		fileList.innerHTML = '<div>Lade Dateien...</div>';
		try {
			const res = await fetch(apiBase + '/all');
			if (!res.ok) throw new Error('Fehler beim Laden');
			const files = await res.json();
			if (!files.length) {
				fileList.innerHTML = '<div>Keine Dateien vorhanden.</div>';
				return;
			}
			fileList.innerHTML = '';
			files.forEach(file => {
				const item = document.createElement('div');
				item.className = 'file-item';
				item.innerHTML = `
					<span class="file-name">${file.name}</span>
					<button class="download-btn" data-id="${file.id}">Download</button>
				`;
				fileList.appendChild(item);
			});
		} catch (err) {
			fileList.innerHTML = '<div>Fehler beim Laden der Dateien.</div>';
		}
	}

	fileList.addEventListener('click', async (e) => {
		if (e.target.classList.contains('download-btn')) {
			const id = e.target.getAttribute('data-id');
			try {
				const res = await fetch(apiBase + `/download/${id}`);
				if (!res.ok) throw new Error('Download fehlgeschlagen');
				const blob = await res.blob();
				const url = window.URL.createObjectURL(blob);
				const a = document.createElement('a');
				a.href = url;
				a.download = e.target.parentElement.querySelector('.file-name').textContent;
				document.body.appendChild(a);
				a.click();
				a.remove();
				window.URL.revokeObjectURL(url);
			} catch (err) {
				message.textContent = err.message;
			}
		}
	});

	loadFiles();
});
