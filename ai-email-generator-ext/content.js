console.log("Hey!! I added my first extension");

// === Shared button style function ===
function styleButton(button) {
  button.style.padding = "10px 20px";
  button.style.border = "none";
  button.style.borderRadius = "999px"; // fully round
  button.style.background = "#0b57d0";
  button.style.color = "#fff";
  button.style.cursor = "pointer";
  button.style.fontSize = "14px";
  button.style.fontWeight = "500";
  button.style.boxShadow = "0 2px 6px rgba(0,0,0,0.15)";
  button.style.transition = "background 0.2s ease";
  button.addEventListener("mouseover", () => button.style.background = "#0949b0");
  button.addEventListener("mouseout", () => button.style.background = "#0b57d0");
}

// === Create AI button dynamically ===
function createAiButton(label = "AI Reply") {
  const button = document.createElement('div');
  button.className = 'T-I J-J5-Ji aoO v7 T-I-atl L3 ai-reply-button';
  button.style.marginRight = '8px';
  button.innerHTML = label;
  button.setAttribute('role', 'button');
  button.setAttribute('data-tooltip', label);

  styleButton(button); // Apply shared style

  return button;
}

// === Create Additional Info Box with Tone Dropdown ===
function createInfoBox(onGenerate) {
  const container = document.createElement('div');
  container.className = 'ai-info-box';
  container.style.marginTop = '8px';
  container.style.padding = '12px';
  container.style.border = '1px solid #e0e0e0';
  container.style.borderRadius = '10px';
  container.style.background = '#fafafa';
  container.style.boxShadow = '0 4px 10px rgba(0,0,0,0.08)';
  container.style.display = 'flex';
  container.style.flexDirection = 'column';
  container.style.gap = '10px';
  container.style.position = 'relative';
  container.style.fontFamily = 'Arial, sans-serif';
  container.style.maxWidth = '100%';
  container.style.boxSizing = 'border-box';

  // Close button
  const closeBtn = document.createElement('span');
  closeBtn.innerHTML = '&times;';
  closeBtn.style.position = 'absolute';
  closeBtn.style.top = '6px';
  closeBtn.style.right = '10px';
  closeBtn.style.cursor = 'pointer';
  closeBtn.style.fontSize = '18px';
  closeBtn.style.color = '#888';
  closeBtn.style.transition = "color 0.2s ease";
  closeBtn.addEventListener('mouseover', () => closeBtn.style.color = "#333");
  closeBtn.addEventListener('mouseout', () => closeBtn.style.color = "#888");
  closeBtn.addEventListener('click', () => container.remove());

  // Label for dropdown
  const toneLabel = document.createElement('label');
  toneLabel.innerText = "Select Tone";
  toneLabel.style.fontSize = "13px";
  toneLabel.style.fontWeight = "500";
  toneLabel.style.color = "#444";

  // Dropdown
  const toneSelect = document.createElement('select');
  toneSelect.style.padding = "8px";
  toneSelect.style.borderRadius = "8px";
  toneSelect.style.border = "1px solid #ccc";
  toneSelect.style.cursor = "pointer";
  toneSelect.style.fontSize = "14px";
  toneSelect.style.outline = "none";
  toneSelect.style.background = "#fff";
  toneSelect.style.transition = "border 0.2s ease";
  toneSelect.addEventListener('focus', () => toneSelect.style.border = "1px solid #0b57d0");
  toneSelect.addEventListener('blur', () => toneSelect.style.border = "1px solid #ccc");

  const tones = ["Professional", "Casual", "Friendly", "Formal", "Straightforward"];
  tones.forEach(t => {
    const option = document.createElement('option');
    option.value = t.toLowerCase();
    option.textContent = t;
    toneSelect.appendChild(option);
  });
  toneSelect.value = "professional";

  // Textarea for additional info
  const textarea = document.createElement('textarea');
  textarea.placeholder = "Add additional instructions...";
  textarea.style.width = '100%';
  textarea.style.minHeight = '70px';
  textarea.style.border = '1px solid #ccc';
  textarea.style.borderRadius = '8px';
  textarea.style.padding = '10px';
  textarea.style.fontSize = '14px';
  textarea.style.lineHeight = '1.5';
  textarea.style.resize = 'vertical';
  textarea.style.boxSizing = 'border-box';
  textarea.style.transition = "border 0.2s ease";
  textarea.addEventListener('focus', () => textarea.style.border = "1px solid #0b57d0");
  textarea.addEventListener('blur', () => textarea.style.border = "1px solid #ccc");

  // Generate button
  const generateBtn = document.createElement('button');
  generateBtn.innerText = "Generate";
  styleButton(generateBtn);
  generateBtn.style.alignSelf = "flex-end";

  generateBtn.addEventListener('click', () => {
    const info = textarea.value.trim();
    const selectedTone = toneSelect.value;
    container.remove();
    onGenerate(info, selectedTone);
  });

  container.appendChild(closeBtn);
  container.appendChild(toneLabel);
  container.appendChild(toneSelect);
  container.appendChild(textarea);
  container.appendChild(generateBtn);
  return container;
}

// === Detect compose vs reply ===
function getButtonLabel() {
  const url = window.location.href;
  if (url.includes("compose=new")) return "AI Write";
  return "AI Reply";
}

// === Find Gmail toolbar ===
function findComposeToolbar() {
  const selectors = ['.btC', '.aDh', '.aDg', '[role="toolbar"]', '.gU.Up'];
  for (const selector of selectors) {
    const toolbar = document.querySelector(selector);
    if (toolbar) return toolbar;
  }
  return null;
}

// === Extract email content (for replies only) ===
function getEmailContent() {
  const selectors = ['.a3s.aiL', '.aH1', '.ii.gt'];
  for (const selector of selectors) {
    const content = document.querySelector(selector);
    if (content) {
      const clone = content.cloneNode(true);
      clone.querySelectorAll('blockquote, .gmail_quote, .gmail_signature').forEach(el => el.remove());
      return clone.innerText.trim();
    }
  }
  return '';
}

// === Inject AI button into Gmail toolbar ===
function injectButton() {
  const existingButton = document.querySelector('.ai-reply-button');
  if (existingButton) existingButton.remove();

  const toolbar = findComposeToolbar();
  if (!toolbar) {
    console.log("Toolbar not found");
    return;
  }

  const label = getButtonLabel();
  const button = createAiButton(label);

  button.addEventListener('click', () => {
    console.log(label + " button clicked!");

    const composeArea = toolbar.parentElement;
    const infoBox = createInfoBox(async (additionalInfo, selectedTone) => {
      try {
        button.innerHTML = "Generating...";
        button.style.pointerEvents = "none";

        let finalContent = "";
        if (!window.location.href.includes("compose=new")) {
          // Reply: include previous email
          const emailContent = getEmailContent();
          finalContent = emailContent + (additionalInfo ? "\n\nAdditional Info: " + additionalInfo : "");
        } else {
          // New compose: only send additional info
          finalContent = additionalInfo || "";
        }

        const response = await fetch('http://localhost:8081/api/email/generate', {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            emailContent: finalContent,
            tone: selectedTone || "professional"
          })
        });

        if (!response.ok) throw new Error("API Request Failed");
        const generatedReply = await response.text();

        const composeBox = document.querySelector('[role="textbox"][g_editable="true"]');
        if (composeBox) {
          composeBox.focus();
          document.execCommand('insertText', false, generatedReply);
        } else console.error('Compose box not found');
      } catch (error) {
        console.error("Error generating reply:", error);
      } finally {
        button.innerHTML = label;
        button.style.pointerEvents = "auto";
      }
    });

    composeArea.appendChild(infoBox);
  });

  toolbar.insertBefore(button, toolbar.firstChild);
}

// === Watch for compose/reply windows ===
const observer = new MutationObserver((mutations) => {
  for (const mutation of mutations) {
    const addedNodes = Array.from(mutation.addedNodes);
    const hasComposeElements = addedNodes.some(node =>
      node.nodeType === Node.ELEMENT_NODE &&
      (node.matches('.aDg, .aDh, .btC, [role="dialog"]') ||
       node.querySelector('.aDg, .aDh, .btC, [role="dialog"]'))
    );
    if (hasComposeElements) {
      console.log("Compose window detected");
      setTimeout(injectButton, 1000);
    }
  }
});
observer.observe(document.body, { childList: true, subtree: true });
