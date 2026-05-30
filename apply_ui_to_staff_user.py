import os
import re

css_to_inject = """
        /* GLASSMORPHISM GLOBAL OVERRIDES */
        body { 
            font-family: 'Inter', sans-serif !important; 
            background: radial-gradient(circle at top left, #e0e7ff, transparent 50%),
                        radial-gradient(circle at top right, #fbcfe8, transparent 50%),
                        radial-gradient(circle at bottom left, #cffafe, transparent 50%),
                        radial-gradient(circle at bottom right, #ede9fe, transparent 50%) !important;
            background-color: #f8fafc !important;
            background-attachment: fixed !important;
            color: #1e293b; 
            min-height: 100vh;
        }
        
        .card-stats, .stat-card, .dashboard-card { 
            background: rgba(255, 255, 255, 0.6) !important;
            backdrop-filter: blur(16px) !important;
            -webkit-backdrop-filter: blur(16px) !important;
            border: 1px solid rgba(255, 255, 255, 0.9) !important; 
            border-radius: 24px !important;
            transition: all 0.3s ease !important; 
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.05) !important;
        }
        .card-stats:hover, .stat-card:hover { transform: translateY(-5px) !important; box-shadow: 0 15px 35px rgba(0,0,0,0.1) !important; }
        
        .modern-card, .card { 
            border-radius: 24px !important; 
            overflow: hidden !important; 
            border: 1px solid rgba(255, 255, 255, 0.8) !important; 
            box-shadow: 0 8px 32px 0 rgba(31, 38, 135, 0.05) !important; 
            background: rgba(255, 255, 255, 0.6) !important; 
            backdrop-filter: blur(16px) !important;
            -webkit-backdrop-filter: blur(16px) !important;
        }
        
        .modern-card .card-header, .card-header {
            background: rgba(255, 255, 255, 0.4) !important;
            border-bottom: 1px solid rgba(255, 255, 255, 0.5) !important;
        }
        
        .modern-card .card-body, .card-body { background: transparent !important; }
        
        .table { background: transparent !important; }
        .table thead th { background: rgba(255, 255, 255, 0.5) !important; border-bottom: 1px solid rgba(255, 255, 255, 0.8) !important; color: #475569 !important; font-weight: 600 !important; text-transform: uppercase !important; font-size: 0.8rem !important; letter-spacing: 0.05em !important; }
        .table tbody tr { background: transparent !important; transition: all 0.2s !important; }
        .table tbody tr:hover { background: rgba(255, 255, 255, 0.8) !important; }
        .table tbody td { border-bottom: 1px solid rgba(255, 255, 255, 0.5) !important; vertical-align: middle !important; }
        
        .form-control, .form-select { border-radius: 12px !important; border: 1px solid rgba(255, 255, 255, 0.8) !important; background: rgba(255, 255, 255, 0.7) !important; font-size: 14px !important; }
        .form-control:focus, .form-select:focus { border-color: #4f46e5 !important; box-shadow: 0 0 0 3px rgba(79, 70, 229, 0.1) !important; background: #fff !important; }
"""

sidebar_css = """
    <style>
        /* Modern Floating & Glassmorphism Sidebar */
        .sidebar { 
            width: 260px; 
            background: rgba(255, 255, 255, 0.4); 
            backdrop-filter: blur(24px);
            -webkit-backdrop-filter: blur(24px);
            border: 1px solid rgba(255, 255, 255, 0.6);
            color: #1e293b; 
            height: calc(100vh - 40px); 
            position: fixed; 
            top: 20px; 
            left: 20px; 
            border-radius: 30px;
            transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); 
            z-index: 1000; 
            overflow-y: auto; 
            box-shadow: 0 15px 35px rgba(31, 38, 135, 0.1); 
        }
        
        /* Custom Scrollbar for Sidebar */
        .sidebar::-webkit-scrollbar { width: 6px; }
        .sidebar::-webkit-scrollbar-track { background: transparent; }
        .sidebar::-webkit-scrollbar-thumb { background: rgba(0,0,0,0.1); border-radius: 10px; }
        
        .sidebar.collapsed { width: 80px; }
        .sidebar-header { display: flex; justify-content: space-between; align-items: center; padding: 24px 20px; border-bottom: 1px solid rgba(0,0,0,0.05); margin-bottom: 10px; }
        .sidebar.collapsed .sidebar-title { display: none; }
        
        .sidebar-title { font-weight: 700; color: #334155; }
        
        .sidebar .nav { list-style: none; padding: 0 10px; margin: 0; display: flex; flex-direction: column; }
        .sidebar .nav-item { margin-bottom: 8px; }
        
        .sidebar .nav-link { 
            color: #475569; 
            padding: 14px 20px; 
            border-radius: 20px;
            transition: all 0.3s; 
            white-space: nowrap; 
            overflow: hidden; 
            display: flex; 
            align-items: center; 
            text-decoration: none; 
            font-size: 15px; 
            font-weight: 600;
        }
        .sidebar .nav-link:hover { 
            color: #3b82f6; 
            background: rgba(255, 255, 255, 0.7); 
            box-shadow: 0 4px 15px rgba(0,0,0,0.05);
            transform: translateX(5px);
        }
        .sidebar .nav-link i { min-width: 32px; font-size: 1.25rem; text-align: center; margin-right: 12px; transition: 0.3s; }
        .sidebar.collapsed .nav-link i { margin-right: 0; font-size: 1.4rem; }
        .sidebar .sidebar-text { transition: opacity 0.3s; }
        .sidebar.collapsed .sidebar-text { opacity: 0; display: none; }
        
        .toggle-btn { background: rgba(255,255,255,0.5); color: #475569; border: 1px solid rgba(0,0,0,0.1); font-size: 1rem; cursor: pointer; padding: 6px 10px; border-radius: 10px; transition: 0.3s; }
        .toggle-btn:hover { background: #fff; color: #3b82f6; box-shadow: 0 4px 10px rgba(0,0,0,0.05); }
        
        .content-area { margin-left: 300px; padding-top: 20px; padding-right: 20px; transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1); min-height: 100vh; }
        .content-area.collapsed { margin-left: 120px; }
    </style>
"""

base_dir = r"C:\Users\ASUS\Desktop\library-management\src\main\resources\templates"
dirs_to_process = ["staff", "user"]

for d in dirs_to_process:
    dir_path = os.path.join(base_dir, d)
    if not os.path.exists(dir_path):
        continue
    
    for filename in os.listdir(dir_path):
        if not filename.endswith('.html'):
            continue
            
        filepath = os.path.join(dir_path, filename)
        with open(filepath, 'r', encoding='utf-8') as f:
            content = f.read()
            
        if filename == 'sidebar.html':
            # Replace <style> block completely
            content = re.sub(r'<style>.*?</style>', sidebar_css, content, flags=re.DOTALL)
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(content)
            continue
            
        # Add Bootstrap if missing
        if 'bootstrap.min.css' not in content and '<head>' in content:
            bs_link = '<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">'
            content = content.replace('<head>', f'<head>\n    {bs_link}')
            
        # Inject Glassmorphism CSS
        if '<style>' in content:
            content = content.replace('<style>', f'<style>{css_to_inject}', 1)
        elif '</head>' in content:
            content = content.replace('</head>', f'<style>{css_to_inject}</style>\n</head>')
            
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)

print("Applied UI changes to staff and user directories.")
