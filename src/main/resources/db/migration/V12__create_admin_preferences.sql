-- Create admin_preferences table
CREATE TABLE admin_preferences (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    dashboard_layout VARCHAR(50) DEFAULT 'DEFAULT',
    preferred_metrics TEXT[] DEFAULT '{}',
    notification_settings JSONB DEFAULT '{"email": true, "sms": false, "in_app": true}',
    theme VARCHAR(20) DEFAULT 'LIGHT' CHECK (theme IN ('LIGHT', 'DARK')),
    rows_per_page SMALLINT DEFAULT 20 CHECK (rows_per_page IN (10, 20, 50, 100)),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_admin_preferences_user_id ON admin_preferences(user_id);

-- Create dashboard_widgets table for customization
CREATE TABLE dashboard_widgets (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    widget_name VARCHAR(100) NOT NULL,
    widget_position SMALLINT,
    is_visible BOOLEAN DEFAULT true,
    configuration JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_dashboard_widgets_user_id ON dashboard_widgets(user_id);
CREATE UNIQUE INDEX uq_dashboard_widgets_user_widget ON dashboard_widgets(user_id, widget_name);
